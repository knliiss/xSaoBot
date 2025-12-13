package dev.knalis.sao_telegram_bot.composer.impl;

import dev.knalis.sao_telegram_bot.composer.ComposerContext;
import dev.knalis.sao_telegram_bot.composer.ContextKey;
import dev.knalis.sao_telegram_bot.composer.intrf.BackComposer;
import dev.knalis.sao_telegram_bot.model.Idea;
import dev.knalis.sao_telegram_bot.model.IdeaReaction;
import dev.knalis.sao_telegram_bot.model.IdeaStatus;
import dev.knalis.sao_telegram_bot.model.ReactionType;
import dev.knalis.sao_telegram_bot.model.user.Role;
import dev.knalis.sao_telegram_bot.model.user.User;
import dev.knalis.sao_telegram_bot.service.intrf.IdeaReactionService;
import dev.knalis.sao_telegram_bot.service.intrf.IdeaService;
import dev.knalis.sao_telegram_bot.service.intrf.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class IdeaDetailMenuComposer implements BackComposer {
    
    IdeaService ideaService;
    UserService userService;
    private final IdeaReactionService ideaReactionService;
    
    @Override
    public String composeText(ComposerContext context) {
        var ideaId = context.get("ideaId");
        var idea = ideaService.findById(Long.parseLong(ideaId)).orElseThrow();
        if (idea == null) {
            return "<b>💡 Идея не найдена</b>.";
        }
        var chatId = context.get(ContextKey.CHAT_ID);
        var user = userService.findById(Long.parseLong(chatId)).orElseThrow();
        return buildIdeaText(idea, user);
    }
    
    @Override
    public List<List<InlineKeyboardButton>> composeButtons(ComposerContext context) {
        var chatId = context.get(ContextKey.CHAT_ID);
        var buttons = new ArrayList<List<InlineKeyboardButton>>();
        var user = userService.findById(Long.parseLong(chatId)).orElseThrow();
        var ideaId = context.get("ideaId");
        var idea = ideaService.findById(Long.parseLong(ideaId)).orElseThrow();
        
        List<InlineKeyboardButton> reactionButtons = new ArrayList<>();
        List<IdeaReaction> reactions = ideaReactionService.findByIdeaId(Long.parseLong(ideaId));
        reactionButtons.add(InlineKeyboardButton.builder()
                .text("👍 " + reactions.stream().filter(r -> r.getReactionType() == ReactionType.LIKE).count())
                .callbackData("idea/react/" + idea.getId() + "/" + ReactionType.LIKE)
                .build());
        reactionButtons.add(InlineKeyboardButton.builder()
                .text("🗑️")
                .callbackData("idea/react/" + idea.getId() + "/remove")
                .build());
        reactionButtons.add(InlineKeyboardButton.builder()
                .text("👎 " + reactions.stream().filter(r -> r.getReactionType() == ReactionType.DISLIKE).count())
                .callbackData("idea/react/" + idea.getId() + "/" + ReactionType.DISLIKE)
                .build());
        buttons.add(reactionButtons);
        
        boolean isAuthorPending = idea.getAuthor() != null && idea.getAuthor().getId() == user.getId() && idea.getStatus() == IdeaStatus.PENDING;
        if (isAuthorPending || user.getRoles().contains(Role.ADMIN)) {
            buttons.add(List.of(
                    InlineKeyboardButton.builder()
                            .text("🗑 Удалить идею")
                            .callbackData("idea/delete/" + idea.getId() + "/" + context.get(ContextKey.PAGE))
                            .build()
            ));
        }
        
        if (user.getRoles().contains(Role.ADMIN)) {
            List<InlineKeyboardButton> adminButtons = new ArrayList<>();
            if (idea.getStatus() == IdeaStatus.PENDING) {
                adminButtons.add(InlineKeyboardButton.builder()
                        .text("✅ Одобрить")
                        .callbackData("idea/moderate/" + idea.getId() + "/approve")
                        .build());
                adminButtons.add(InlineKeyboardButton.builder()
                        .text("❌ Отклонить")
                        .callbackData("idea/moderate/" + idea.getId() + "/reject")
                        .build());
            } else if (idea.getStatus() == IdeaStatus.APPROVED) {
                adminButtons.add(InlineKeyboardButton.builder()
                        .text("❌ Отклонить")
                        .callbackData("idea/moderate/" + idea.getId() + "/reject")
                        .build());
                adminButtons.add(InlineKeyboardButton.builder()
                        .text("🏁 Завершить")
                        .callbackData("idea/moderate/" + idea.getId() + "/complete")
                        .build());
            } else if (idea.getStatus() == IdeaStatus.REJECTED) {
                adminButtons.add(InlineKeyboardButton.builder()
                        .text("✅ Одобрить")
                        .callbackData("idea/moderate/" + idea.getId() + "/approve")
                        .build());
            }
            buttons.add(adminButtons);
        }
        
        buttons.add(generateBackButton(context, "menu/" + chatId + "/idea"));
        return buttons;
    }
    
    
    private String buildIdeaText(Idea idea, User user) {
        StringBuilder sb = new StringBuilder();
        sb.append("<b>💡 Идея №").append(idea.getId()).append("</b>\n");
        sb.append(idea.getTitle() != null ? idea.getTitle() : "Без названия").append("\n\n");
        sb.append("<i>Автор: ").append(idea.getAuthor() != null ? idea.getAuthor().getUsername() : "Неизвестен").append("</i>").append("\n\n");
        if (user.getRoles().contains(Role.ADMIN)) {
            sb.append(" (ID: ").append(idea.getAuthor() != null ? idea.getAuthor().getId() : "N/A").append(")").append("\n\n");
        }
        
        sb.append("<b>Идея:</b> ").append(idea.getContent() != null ? idea.getContent() : "(описание отсутствует)").append("\n\n");
        sb.append("<b>Статус:</b> ").append(idea.getStatus() != null ? idea.getStatus().getVisualName() : "Не задан").append("\n");
        sb.append("<b>Дата создания:</b> ").append(formatDate(idea.getCreatedAt())).append("\n");
        
        var reactions = ideaReactionService.findByIdeaId(idea.getId());
        var likes = reactions.stream().filter(r -> r.getReactionType() == ReactionType.LIKE).count();
        var dislikes = reactions.size() - likes;
        sb.append("<b>Реакции:</b> 👍 ").append(likes).append(" | 👎 ").append(dislikes).append("\n");
        return sb.toString();
    }
    
    private String formatDate(Instant instant) {
        if (instant == null) return "N/A";
        ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return zdt.format(formatter);
    }
}

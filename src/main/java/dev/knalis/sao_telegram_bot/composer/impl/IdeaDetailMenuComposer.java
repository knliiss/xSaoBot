package dev.knalis.sao_telegram_bot.composer.impl;

import dev.knalis.sao_telegram_bot.composer.intrf.BackComposer;
import dev.knalis.sao_telegram_bot.context.ComposerContext;
import dev.knalis.sao_telegram_bot.context.ContextKey;
import dev.knalis.sao_telegram_bot.context.RequiresContext;
import dev.knalis.sao_telegram_bot.dto.telegram.Button;
import dev.knalis.sao_telegram_bot.dto.telegram.ButtonRow;
import dev.knalis.sao_telegram_bot.model.Idea;
import dev.knalis.sao_telegram_bot.model.IdeaReaction;
import dev.knalis.sao_telegram_bot.model.IdeaStatus;
import dev.knalis.sao_telegram_bot.model.ReactionType;
import dev.knalis.sao_telegram_bot.model.user.Role;
import dev.knalis.sao_telegram_bot.model.user.User;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiresContext({ContextKey.IDEA, ContextKey.USER, ContextKey.USER_ID, ContextKey.BACK_PAGE, ContextKey.IDEA_REACTIONS})
public class IdeaDetailMenuComposer implements BackComposer {
    
    @Override
    public String composeText(ComposerContext context) {
        var user = (User) context.get(ContextKey.USER);
        var idea = (Idea) context.get(ContextKey.IDEA);
        List<IdeaReaction> reactions = context.get(ContextKey.IDEA_REACTIONS);
        return buildIdeaText(idea, user, reactions);
    }
    
    @Override
    public List<ButtonRow> composeButtons(ComposerContext context) {
        var buttons = new ArrayList<ButtonRow>();
        var reactionButtonRow = new ButtonRow();
        var userId = context.get(ContextKey.USER_ID);
        var user = (User) context.get(ContextKey.USER);
        var idea = (Idea) context.get(ContextKey.IDEA);
        var backPage = context.get(ContextKey.BACK_PAGE);
        
        List<IdeaReaction> reactions = context.get(ContextKey.IDEA_REACTIONS);
        reactionButtonRow.add(Button.builder()
                .text("👍 " + reactions.stream().filter(r -> r.getReactionType() == ReactionType.LIKE).count())
                .callbackData("idea/react/" + idea.getId() + "/" + ReactionType.LIKE)
                .build());
        reactionButtonRow.add(Button.builder()
                .text("🗑️")
                .callbackData("idea/react/" + idea.getId() + "/remove")
                .build());
        reactionButtonRow.add(Button.builder()
                .text("👎 " + reactions.stream().filter(r -> r.getReactionType() == ReactionType.DISLIKE).count())
                .callbackData("idea/react/" + idea.getId() + "/" + ReactionType.DISLIKE)
                .build());
        buttons.add(reactionButtonRow);
        
        boolean isAuthorPending = idea.getAuthor() != null && idea.getAuthor().getId() == user.getId() && idea.getStatus() == IdeaStatus.PENDING;
        if (isAuthorPending || user.getRoles().contains(Role.ADMIN)) {
            buttons.add(ButtonRow.of(
                    Button.builder()
                            .text("🗑 Удалить идею")
                            .callbackData("idea/delete/" + idea.getId() + "/" + backPage)
                            .build()
            ));
        }
        
        if (user.getRoles().contains(Role.ADMIN)) {
            var adminButtonRow = new ButtonRow();
            if (idea.getStatus() == IdeaStatus.PENDING) {
                adminButtonRow.add(Button.builder()
                        .text("✅ Одобрить")
                        .callbackData("idea/moderate/" + idea.getId() + "/approve")
                        .build());
                adminButtonRow.add(Button.builder()
                        .text("❌ Отклонить")
                        .callbackData("idea/moderate/" + idea.getId() + "/reject")
                        .build());
            } else if (idea.getStatus() == IdeaStatus.APPROVED) {
                adminButtonRow.add(Button.builder()
                        .text("❌ Отклонить")
                        .callbackData("idea/moderate/" + idea.getId() + "/reject")
                        .build());
                adminButtonRow.add(Button.builder()
                        .text("🏁 Завершить")
                        .callbackData("idea/moderate/" + idea.getId() + "/complete")
                        .build());
            } else if (idea.getStatus() == IdeaStatus.REJECTED) {
                adminButtonRow.add(Button.builder()
                        .text("✅ Одобрить")
                        .callbackData("idea/moderate/" + idea.getId() + "/approve")
                        .build());
            }
            buttons.add(adminButtonRow);
        }
        
        buttons.add(generateBackButton("menu/" + userId + "/idea/" + backPage));
        return buttons;
    }
    
    
    private String buildIdeaText(Idea idea, User user, List<IdeaReaction> reactions) {
        StringBuilder sb = new StringBuilder();
        sb.append("<b>💡 Идея \n\uD83C\uDD94 ID:").append(idea.getId()).append("</b>");
        sb.append(idea.getTitle() != null ? idea.getTitle() : "Без названия").append("\n\n");
        sb.append("<i>👤Автор: ").append(idea.getAuthor().getNickname() != null ? idea.getAuthor().getNickname() : "Неизвестен").append("</i>").append("\n\n");
        if (user.getRoles().contains(Role.ADMIN)) {
            sb.append(" (Юз автора: @").append(idea.getAuthor() != null ? idea.getAuthor().getUsername() + " (" + idea.getAuthor().getId() : "N/A").append(")").append("\n\n");
        }
        
        sb.append("<b>📝Описание:</b> ").append(idea.getContent() != null ? idea.getContent() : "(описание отсутствует)").append("\n\n");
        sb.append("<b>📌Статус:</b> ").append(idea.getStatus() != null ? idea.getStatus().getVisualName() : "Не задан").append("\n");
        sb.append("<b>🕓Дата создания:</b> ").append(formatDate(idea.getCreatedAt())).append("\n");
        
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

package dev.knalis.sao_telegram_bot.composer.impl;

import dev.knalis.sao_telegram_bot.composer.ComposerContext;
import dev.knalis.sao_telegram_bot.composer.ContextKey;
import dev.knalis.sao_telegram_bot.composer.intrf.BackComposer;
import dev.knalis.sao_telegram_bot.composer.intrf.ListableComposer;
import dev.knalis.sao_telegram_bot.composer.intrf.PageComposer;
import dev.knalis.sao_telegram_bot.dto.telegram.Button;
import dev.knalis.sao_telegram_bot.model.Idea;
import dev.knalis.sao_telegram_bot.model.IdeaStatus;
import dev.knalis.sao_telegram_bot.service.intrf.IdeaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class IdeaMenuComposer implements PageComposer, ListableComposer<Idea>, BackComposer {
    
    private final IdeaService ideaService;
    
    private static final int PAGE_SIZE = 5;
    
    @Override
    public String composeText(ComposerContext context) {
        int page = Integer.parseInt(context.getOrDefault(ContextKey.PAGE.toString(), "1"));
        long userId = Long.parseLong(context.get(ContextKey.CHAT_ID));
        
        List<Idea> userIdeas = ideaService.findAll().stream()
                .filter(i -> i.getAuthor() != null && i.getAuthor().getId() == userId)
                .toList();
        
        int total = userIdeas.size();
        int totalPages = Math.max(1, (int) Math.ceil((double) total / PAGE_SIZE));
        
        return """
                <b>💡 Предложенные идеи</b>
                
                Страница: %d / %d
                Всего идей: <b>%d</b>
                
                ⏳ — на рассмотрении
                ✅ — одобрено
                ❌ — отклонено
                ⭐ — реализовано
                """.formatted(page, totalPages, total);
    }
    
    @Override
    public List<List<InlineKeyboardButton>> composeButtons(ComposerContext context) {
        int page = Integer.parseInt(context.getOrDefault(ContextKey.PAGE.toString(), "1"));
        long userId = Long.parseLong(context.get(ContextKey.CHAT_ID));
        
        List<Idea> all = ideaService.findAll().stream()
                .filter(i -> i.getAuthor() != null && i.getAuthor().getId() == userId)
                .collect(Collectors.toList());
        
        sortIdeasByStatus(all);
        
        int total = all.size();
        int totalPages = Math.max(1, (int) Math.ceil((double) total / PAGE_SIZE));
        
        int from = Math.max(0, (page - 1) * PAGE_SIZE);
        int to = Math.min(total, from + PAGE_SIZE);
        List<Idea> ideas = from < to ? all.subList(from, to) : List.of();
        
        Function<Idea, String> callbackMapper =
                idea -> "menu/" + userId + "/idea/" + idea.getId() + "/" + page;
        
        Function<Idea, String> textMapper = idea -> {
            String title = idea.getTitle();
            if (title.length() > 32) {
                title = title.substring(0, 32) + "…";
            }
            return getStatusEmoji(idea.getStatus()) + " " + title;
        };
        
        
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(Collections.singletonList(
                Button.builder()
                        .text("💡 Предложить идею")
                        .callbackData("idea/create")
                        .build()
                        .toInlineButton()
        ));
        
        rows.addAll(buildListOfTypeButtons(ideas, 1, callbackMapper, textMapper));
        
        rows.add(generateFooter(
                "menu/" + context.get(ContextKey.CHAT_ID) + "/idea/",
                page,
                totalPages
        ));
        
        rows.add(generateBackButton(context, "menu/" + context.get(ContextKey.CHAT_ID)));
        
        return rows;
    }
    
    private void sortIdeasByStatus(List<Idea> ideas) {
        if (ideas == null || ideas.size() <= 1) return;
        
        ideas.sort((i1, i2) -> {
            if (i1.getStatus() == i2.getStatus()) {
                return Long.compare(i1.getId(), i2.getId());
            }
            if (i1.getStatus() == null) return 1;
            if (i2.getStatus() == null) return -1;
            return Integer.compare(
                    i1.getStatus().getPriority(),
                    i2.getStatus().getPriority()
            );
        });
    }
    
    private String getStatusEmoji(IdeaStatus status) {
        if (status == null) return "❔";
        return switch (status) {
            case PENDING -> "⏳";
            case APPROVED -> "✅";
            case REJECTED -> "❌";
            case COMPLETED -> "⭐";
        };
    }
}
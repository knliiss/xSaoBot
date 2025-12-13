package dev.knalis.sao_telegram_bot.composer.impl;

import dev.knalis.sao_telegram_bot.composer.intrf.BackComposer;
import dev.knalis.sao_telegram_bot.composer.intrf.ListableComposer;
import dev.knalis.sao_telegram_bot.composer.intrf.PageComposer;
import dev.knalis.sao_telegram_bot.context.ComposerContext;
import dev.knalis.sao_telegram_bot.context.ContextKey;
import dev.knalis.sao_telegram_bot.context.RequiresContext;
import dev.knalis.sao_telegram_bot.dto.telegram.Button;
import dev.knalis.sao_telegram_bot.dto.telegram.ButtonRow;
import dev.knalis.sao_telegram_bot.model.Idea;
import dev.knalis.sao_telegram_bot.model.IdeaStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@RequiresContext({ContextKey.IDEAS, ContextKey.USER_ID, ContextKey.PAGE})
public class IdeaMenuComposer implements PageComposer, ListableComposer<Idea>, BackComposer {
    
    private static final int PAGE_SIZE = 5;
    
    @Override
    public String composeText(ComposerContext context) {
        var items = (List<Idea>) context.get(ContextKey.IDEAS);
        var state = buildState(context, items);
        var userId = context.get(ContextKey.USER_ID);
        int userIdeaCount = (int) items.stream().filter(idea -> idea.getAuthor().getId() == (long) userId).count();
        
        return """
                <b>💡 Предложенные идеи</b>
                
                Страница: %d / %d
                Всего идей: <b>%d</b>
                Ваших идей: <b>%d</b>
                
                ⏳ — на рассмотрении
                ✅ — одобрено
                ❌ — отклонено
                ⭐ — реализовано
                """.formatted(state.page(), state.totalPages(), items.size(), userIdeaCount);
    }
    
    @Override
    public List<ButtonRow> composeButtons(ComposerContext context) {
        var items = (List<Idea>) context.get(ContextKey.IDEAS);
        sortIdeasByStatus(items);
        var state = buildState(context, items);
  
        List<ButtonRow> rows = new ArrayList<>();
        rows.add(ButtonRow.of(
                Button.builder()
                        .text("💡 Предложить идею")
                        .callbackData("idea/create")
                        .build()
        ));
        
        rows.addAll(buildListOfTypeButtons(items, 1, context));
        
        rows.add(generateFooter(
                "menu/" + context.get(ContextKey.USER_ID) + "/idea/",
                state.page(),
                state.totalPages()
        ));
        
        rows.add(generateBackButton("menu/" + context.get(ContextKey.USER_ID)));
        
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
    
    @Override
    public Button buildItemButton(Idea item, int index, ComposerContext context) {
        String statusEmoji = getStatusEmoji(item.getStatus());
        String text = "%d. %s %s".formatted(index, statusEmoji, item.getTitle());
        String callbackData = "menu/" + context.get(ContextKey.USER_ID) + "/idea/" + item.getId() + "/" + context.get(ContextKey.PAGE);
        return Button.builder()
                .text(text)
                .callbackData(callbackData)
                .build();
    }
    
    @Override
    public int getPageSize() {
        return PAGE_SIZE;
    }
}
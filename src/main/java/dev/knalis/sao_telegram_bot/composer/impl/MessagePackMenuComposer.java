package dev.knalis.sao_telegram_bot.composer.impl;

import dev.knalis.sao_telegram_bot.composer.intrf.BackComposer;
import dev.knalis.sao_telegram_bot.composer.intrf.ListableComposer;
import dev.knalis.sao_telegram_bot.composer.intrf.PageComposer;
import dev.knalis.sao_telegram_bot.context.ComposerContext;
import dev.knalis.sao_telegram_bot.context.ContextKey;
import dev.knalis.sao_telegram_bot.context.RequiresContext;
import dev.knalis.sao_telegram_bot.dto.telegram.Button;
import dev.knalis.sao_telegram_bot.dto.telegram.ButtonRow;
import dev.knalis.sao_telegram_bot.model.user.settings.MessagePack;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@RequiresContext({ContextKey.MESSAGEPACKS, ContextKey.USER_ID, ContextKey.PAGE})
public class MessagePackMenuComposer implements ListableComposer<MessagePack>, PageComposer, BackComposer {
    
    private static final int PAGE_SIZE = 12;

    @Override
    public String composeText(ComposerContext context) {
        var items = (List<MessagePack>) context.get(ContextKey.MESSAGEPACKS);
        var state = buildState(context, items);
        return "<b>📦 Пакеты сообщений</b>\n\nСтраница: " + state.page() + "/" + state.totalPages() + " — всего пакетов: " + items.size() + "\nВыберите пакет, чтобы посмотреть детали и купить.";
    }

    @Override
    public List<ButtonRow> composeButtons(ComposerContext context) {
        var items = (List<MessagePack>) context.get(ContextKey.MESSAGEPACKS);
        var state = buildState(context, items);
        var userId = context.get(ContextKey.USER_ID);
        
        List<ButtonRow> rows = new ArrayList<>(buildListOfTypeButtons(state.items(), 2, context));
        rows.add(generateFooter("menu/" + userId + "/messagepack/", state.page(), state.totalPages()));
        rows.add(generateBackButton("menu/" + userId));
        return rows;
    }
    
    @Override
    public Button buildItemButton(MessagePack item, int index, ComposerContext context) {
        var userId = context.get(ContextKey.USER_ID);
        return Button.builder()
                .text((item.getEmoji() != null ? item.getEmoji() + " " : "") + item.getName() + " — " + item.getCost() + "💰")
                .callbackData("menu/" + userId + "/messagepack/" + item.getId() + "/" + context.get(ContextKey.PAGE))
                .build();
    }
    
    @Override
    public int getPageSize() {
        return PAGE_SIZE;
    }
}

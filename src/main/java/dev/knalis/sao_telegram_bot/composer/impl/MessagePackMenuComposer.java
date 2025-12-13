package dev.knalis.sao_telegram_bot.composer.impl;

import dev.knalis.sao_telegram_bot.composer.ComposerContext;
import dev.knalis.sao_telegram_bot.composer.ContextKey;
import dev.knalis.sao_telegram_bot.composer.intrf.ListableComposer;
import dev.knalis.sao_telegram_bot.composer.intrf.PageComposer;
import dev.knalis.sao_telegram_bot.dto.telegram.Button;
import dev.knalis.sao_telegram_bot.model.user.settings.MessagePack;
import dev.knalis.sao_telegram_bot.service.intrf.MessagePackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class MessagePackMenuComposer implements ListableComposer<MessagePack>, PageComposer {
    private final MessagePackService messagePackService;
    private static final int PAGE_SIZE = 6;

    @Override
    public String composeText(ComposerContext context) {
        String pageStr = context.getOrDefault(ContextKey.PAGE.toString(), "1");
        int page = Integer.parseInt(pageStr);
        int total = messagePackService.getAll().size();
        int totalPage = Math.max(1, (int) Math.ceil((double) total / PAGE_SIZE));
        return "<b>📦 Пакеты сообщений</b>\n\nСтраница: " + page + "/" + totalPage + " — всего пакетов: " + total + "\nВыберите пакет, чтобы посмотреть детали и купить.";
    }

    @Override
    public List<List<InlineKeyboardButton>> composeButtons(ComposerContext context) {
        String pageStr = context.getOrDefault(ContextKey.PAGE.toString(), "1");
        int page = Integer.parseInt(pageStr);
        List<MessagePack> all = messagePackService.getAll();
        int total = all.size();
        int totalPage = Math.max(1, (int) Math.ceil((double) total / PAGE_SIZE));

        int from = Math.max(0, (page - 1) * PAGE_SIZE);
        int to = Math.min(total, from + PAGE_SIZE);
        List<MessagePack> packs = from < to ? all.subList(from, to) : List.of();

        Function<MessagePack, String> callbackMapper = pack -> "menu/" + context.get(ContextKey.CHAT_ID) + "/messagepack/" + pack.getId() + "/" + page;
        Function<MessagePack, String> textMapper = pack -> (pack.getEmoji() != null ? pack.getEmoji() + " " : "") + pack.getName() + " — " + pack.getCost() + "💰";

        List<List<InlineKeyboardButton>> rows = new ArrayList<>(buildListOfTypeButtons(packs, 1, callbackMapper, textMapper));
        rows.add(generateFooter("menu/" + context.get(ContextKey.CHAT_ID) + "/messagepack/", page, totalPage));
        rows.add(List.of(Button.builder().text("🏠 Меню").callbackData("menu/" + context.get(ContextKey.CHAT_ID)).build().toInlineButton()));
        return rows;
    }
}

package dev.knalis.sao_telegram_bot.composer.intrf;

import dev.knalis.sao_telegram_bot.composer.ContextKey;
import dev.knalis.sao_telegram_bot.composer.ComposerContext;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public interface Composer {

    default SendMessage compose(ComposerContext context) {
        String chatId;
        try {
            chatId = context.get(ContextKey.CHAT_ID);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Missing CHAT_ID in context");
        }

        List<List<InlineKeyboardButton>> buttons = composeButtons(context);
        InlineKeyboardMarkup markup = null;
        if (buttons != null && !buttons.isEmpty()) {
            markup = new InlineKeyboardMarkup(buttons);
        }

        return SendMessage.builder()
                .chatId(chatId)
                .text(composeText(context))
                .replyMarkup(markup)
                .build();
    }

    String composeText(ComposerContext context);

    List<List<InlineKeyboardButton>> composeButtons(ComposerContext context);

}
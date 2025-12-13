package dev.knalis.sao_telegram_bot.composer.intrf;

import dev.knalis.sao_telegram_bot.context.ComposerContext;
import dev.knalis.sao_telegram_bot.context.ContextKey;
import dev.knalis.sao_telegram_bot.dto.telegram.ButtonRow;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

import static dev.knalis.sao_telegram_bot.util.TextLayout.formatWithFiller;

public interface Composer {
    
    default SendMessage compose(ComposerContext context) {
        long chatId;
        try {
            chatId = context.get(ContextKey.USER_ID);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Missing CHAT_ID in context");
        }
        
        var buttons = composeButtons(context);
        InlineKeyboardMarkup markup = null;
        if (buttons != null && !buttons.isEmpty()) {
            markup = new InlineKeyboardMarkup(buttons.stream()
                    .map(ButtonRow::toInlineKeyboardButtons)
                    .toList());
        }
        
        String rawText = composeText(context);
        String text = formatWithFiller(rawText);
        
        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(markup)
                .build();
    }

    String composeText(ComposerContext context);

    List<ButtonRow> composeButtons(ComposerContext context);

}
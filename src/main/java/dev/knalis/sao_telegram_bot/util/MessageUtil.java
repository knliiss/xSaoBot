package dev.knalis.sao_telegram_bot.util;

import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@UtilityClass
public final class MessageUtil {
    public static EditMessageText editMessageFrom(SendMessage message, int messageId) {
        EditMessageText edit = new EditMessageText();
        edit.setChatId(message.getChatId());
        edit.setText(message.getText());
        if (message.getReplyMarkup() instanceof InlineKeyboardMarkup inlineKeyboard) {
            edit.setReplyMarkup(inlineKeyboard);
        }

        edit.setMessageId(messageId);
        return edit;
    }
}

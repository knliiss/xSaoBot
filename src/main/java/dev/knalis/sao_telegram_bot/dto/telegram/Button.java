package dev.knalis.sao_telegram_bot.dto.telegram;

import lombok.Builder;
import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@Data
@Builder
public class Button {

    private String text;
    private String callbackData;

    public InlineKeyboardButton toInlineButton() {
        var inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(text);
        inlineKeyboardButton.setCallbackData(callbackData);
        return inlineKeyboardButton;
    }

}

package dev.knalis.sao_telegram_bot.util;

import dev.knalis.sao_telegram_bot.dto.Button;
import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public final class KeyboardUtil {

    public static List<InlineKeyboardButton> formCallBackButtonsRow(Button... buttons) {
        List<InlineKeyboardButton> row = new ArrayList<>(buttons.length);
        for (Button b : buttons) {
            if (b != null) {
                InlineKeyboardButton btn = new InlineKeyboardButton();
                btn.setText(b.getText());
                btn.setCallbackData(b.getCallbackData());
                row.add(btn);
            }
        }
        return row;
    }

    public static List<List<InlineKeyboardButton>> formCallbackButtons(List<InlineKeyboardButton>... rows) {
        return new ArrayList<>(List.of(rows));
    }
}

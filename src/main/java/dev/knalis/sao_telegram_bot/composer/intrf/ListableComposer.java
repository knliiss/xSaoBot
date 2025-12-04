package dev.knalis.sao_telegram_bot.composer.intrf;

import dev.knalis.sao_telegram_bot.dto.Button;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public interface ListableComposer<T> extends Composer {

    default List<List<InlineKeyboardButton>> buildListOfTypeButtons(
            List<T> items,
            int perRow,
            Function<T, String> callbackMapper,
            Function<T, String> textMapper
    ) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> currentRow = new ArrayList<>();

        for (T item : items) {
            InlineKeyboardButton button = Button.builder()
                    .text(textMapper.apply(item))
                    .callbackData(callbackMapper.apply(item))
                    .build()
                    .toInlineButton();

            currentRow.add(button);

            if (currentRow.size() == perRow) {
                rows.add(currentRow);
                currentRow = new ArrayList<>();
            }
        }

        if (!currentRow.isEmpty()) {
            rows.add(currentRow);
        }

        return rows;
    }
}
package dev.knalis.sao_telegram_bot.composer.intrf;

import dev.knalis.sao_telegram_bot.dto.Button;
import dev.knalis.sao_telegram_bot.util.KeyboardUtil;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public interface PageComposer extends Composer {

    default List<InlineKeyboardButton> generateFooter(String callBackUrl, int page, int totalPage) {
        if (totalPage <= 1) return List.of();

        if (!callBackUrl.endsWith("/")) {
            callBackUrl += "/";
        }

        int prevPage2 = Math.max(1, page - 2);
        int prevPage = Math.max(1, page - 1);
        int nextPage = Math.min(totalPage, page + 1);
        int nextPage2 = Math.min(totalPage, page + 2);

        Button indicator = Button.builder()
                .text(page + "/" + totalPage)
                .callbackData("noop")
                .build();

        List<Button> buttons = new ArrayList<>();

        if (page > 1) {
            buttons.add(Button.builder().text("◀️◀️").callbackData(callBackUrl + prevPage2).build());
            buttons.add(Button.builder().text("◀️").callbackData(callBackUrl + prevPage).build());
        }

        buttons.add(indicator);

        if (page < totalPage) {
            buttons.add(Button.builder().text("▶️").callbackData(callBackUrl + nextPage).build());
            buttons.add(Button.builder().text("▶️▶️").callbackData(callBackUrl + nextPage2).build());
        }

        return KeyboardUtil.formCallBackButtonsRow(buttons.toArray(Button[]::new));
    }


}

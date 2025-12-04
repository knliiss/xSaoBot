package dev.knalis.sao_telegram_bot.composer.intrf;

import dev.knalis.sao_telegram_bot.composer.ComposerContext;
import dev.knalis.sao_telegram_bot.composer.ContextKey;
import dev.knalis.sao_telegram_bot.dto.Button;
import dev.knalis.sao_telegram_bot.util.KeyboardUtil;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public interface BackComposer extends Composer {

    default List<InlineKeyboardButton> generateBackButton(String backCallback) {
        return KeyboardUtil.formCallBackButtonsRow(Button.builder().text("🔙 Назад").callbackData(backCallback).build());
    }

    default List<InlineKeyboardButton> generateBackButton(ComposerContext context, String fallbackCallback) {
        String back = context.containsKey(ContextKey.BACK_CALLBACK_URL.toString())
                ? context.get(ContextKey.BACK_CALLBACK_URL)
                : fallbackCallback;
        return generateBackButton(back);
    }
}

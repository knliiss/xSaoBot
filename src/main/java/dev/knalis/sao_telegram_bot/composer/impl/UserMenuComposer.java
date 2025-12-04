package dev.knalis.sao_telegram_bot.composer.impl;

import dev.knalis.sao_telegram_bot.composer.ComposerContext;
import dev.knalis.sao_telegram_bot.composer.ContextKey;
import dev.knalis.sao_telegram_bot.composer.intrf.BackComposer;
import dev.knalis.sao_telegram_bot.dto.Button;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

import static dev.knalis.sao_telegram_bot.util.KeyboardUtil.formCallbackButtons;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserMenuComposer implements BackComposer {

    @Override
    public String composeText(ComposerContext context) {
        String chatIdStr = context.get(ContextKey.CHAT_ID);
        // To avoid tight coupling with database model during merge, show a minimal safe profile.
        return "<b>👤 Информация о пользователе</b>\n\nID: <code>" + chatIdStr + "</code>\nДетальная информация временно недоступна.";
    }


    @Override
    public List<List<InlineKeyboardButton>> composeButtons(ComposerContext context) {
        String chatIdStr = context.get(ContextKey.CHAT_ID);
        return formCallbackButtons(
                List.of(
                        Button.builder().callbackData("user/" + chatIdStr + "/location").text("📍Изменить локацию").build().toInlineButton()
                ),
                List.of(
                    Button.builder().callbackData("reminder/" + chatIdStr).text("🔔 Напоминания").build().toInlineButton()
                ),
                List.of(
                        Button.builder().callbackData("user/" + chatIdStr + "/account").text("⚙️ Управление аккаунтами").build().toInlineButton()
                ),
                generateBackButton(context, "message/menu")
        );
    }
}

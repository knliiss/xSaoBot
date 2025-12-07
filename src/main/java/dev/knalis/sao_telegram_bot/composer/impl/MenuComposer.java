package dev.knalis.sao_telegram_bot.composer.impl;

import dev.knalis.sao_telegram_bot.composer.ContextKey;
import dev.knalis.sao_telegram_bot.composer.intrf.BackComposer;
import dev.knalis.sao_telegram_bot.composer.ComposerContext;
import dev.knalis.sao_telegram_bot.dto.Button;
import dev.knalis.sao_telegram_bot.service.crud.impl.UserService;
import dev.knalis.sao_telegram_bot.util.KeyboardUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MenuComposer implements BackComposer {

    UserService userService;

    @Override
    public String composeText(ComposerContext context) {
        return "<b>📋 Главное меню</b>\n\nВыберите раздел." +
                "\nЕсли нужна помощь — напишите администратору или используйте /help.";
    }


    @Override
    public List<List<InlineKeyboardButton>> composeButtons(ComposerContext context) {
        var chatId = context.get(ContextKey.CHAT_ID);
        var user = userService.findById(Long.parseLong(chatId));

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.addAll(KeyboardUtil.formCallbackButtons(
                List.of(Button.builder().callbackData("menu/" + chatId + "/user").text("👤 Аккаунт").build().toInlineButton()),
                List.of(Button.builder().callbackData("menu/" + chatId + "/messagepack/1").text("🛒 Магазин").build().toInlineButton()),
                List.of(Button.builder().callbackData("menu/" + chatId + "/idea/1").text("💡 Идеи").build().toInlineButton()),
                List.of(Button.builder().callbackData("menu/" + chatId + "/gang").text("👥 Банды").build().toInlineButton()),
                List.of(Button.builder().callbackData("menu/" + chatId + "/settings/OTHER").text("⚙️ Настройки").build().toInlineButton())
        ));

        if (user != null && user.getRoles() != null && user.getRoles().stream().anyMatch(r -> r.name().equalsIgnoreCase("ADMIN"))) {
            rows.add(List.of(Button.builder()
                    .callbackData("menu/" + chatId + "/idea/1")
                    .text("🛡️ Админ — идеи")
                    .build().toInlineButton()));
        }

        rows.add(generateBackButton(context, "message/delete"));
        return rows;
    }
}

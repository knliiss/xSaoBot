package dev.knalis.sao_telegram_bot.composer.impl;

import dev.knalis.sao_telegram_bot.composer.intrf.BackComposer;
import dev.knalis.sao_telegram_bot.context.ComposerContext;
import dev.knalis.sao_telegram_bot.context.ContextKey;
import dev.knalis.sao_telegram_bot.context.RequiresContext;
import dev.knalis.sao_telegram_bot.dto.telegram.Button;
import dev.knalis.sao_telegram_bot.dto.telegram.ButtonRow;
import dev.knalis.sao_telegram_bot.model.user.Role;
import dev.knalis.sao_telegram_bot.model.user.User;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@RequiresContext({ContextKey.USER_ID, ContextKey.USER})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MenuComposer implements BackComposer {


    @Override
    public String composeText(ComposerContext context) {
        return "<b>📋 Главное меню</b>\n\nВыберите раздел." +
                "\nЕсли нужна помощь — напишите администратору или используйте /help.";
    }


    @Override
    public List<ButtonRow> composeButtons(ComposerContext context) {
        var userId = context.get(ContextKey.USER_ID);
        var user = (User) context.get(ContextKey.USER);
        
        List<ButtonRow> rows = new ArrayList<>();
        rows.add(ButtonRow.of(Button.builder().callbackData("menu/" + userId + "/user").text("👤 Аккаунт").build()));
        rows.add(ButtonRow.of(Button.builder().callbackData("menu/" + userId + "/messagepack/1").text("🛒 Магазин").build()));
        rows.add(ButtonRow.of(Button.builder().callbackData("menu/" + userId + "/idea/1").text("💡 Идеи").build()));
        rows.add(ButtonRow.of(Button.builder().callbackData("menu/" + userId + "/gang").text("👥 Банды").build()));
        rows.add(ButtonRow.of(Button.builder().callbackData("menu/" + userId + "/settings/OTHER").text("⚙️ Настройки").build()));
        
        
        if (user.getRoles().contains(Role.ADMIN)) {
            rows.add(ButtonRow.of(Button.builder().callbackData("menu/" + userId + "/idea/1").text("🛡️ Админ — идеи").build()));
        }
        
        rows.add(generateBackButton("message/delete"));
        return rows;
    }
}

package dev.knalis.sao_telegram_bot.composer.impl;

import dev.knalis.sao_telegram_bot.composer.intrf.BackComposer;
import dev.knalis.sao_telegram_bot.context.ComposerContext;
import dev.knalis.sao_telegram_bot.context.ContextKey;
import dev.knalis.sao_telegram_bot.context.RequiresContext;
import dev.knalis.sao_telegram_bot.dto.telegram.Button;
import dev.knalis.sao_telegram_bot.dto.telegram.ButtonRow;
import dev.knalis.sao_telegram_bot.model.user.User;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Component
@RequiredArgsConstructor
@RequiresContext({ContextKey.USER, ContextKey.USER_ID})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserMenuComposer implements BackComposer {
    
    @Override
    public String composeText(ComposerContext context) {
        var user = (User) context.get(ContextKey.USER);
            var created = user.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate();
            var nickname = user.getNickname() == null ? "—" : "@" + user.getNickname();
            var gang = user.getGang() == null ? "—" : user.getGang().getName();
            var subscription = user.getSubscription() == null ? "—" : user.getSubscription().getPlan().name();
            
            return """
                    <b>👤 Профиль игрока</b>
                    
                    <b>ID:</b> <code>%d</code>
                    <b>Никнейм:</b> %s
                    <b>Локация:</b> %d
                    <b>Баланс:</b> %.2f 💰
                    <b>Подписка:</b> %s
                    <b>Гильдия:</b> %s
                    <b>Дата регистрации:</b> %s

                    <b>Активный пакет сообщений:</b> %s
                    """.formatted(
                    user.getId(),
                    nickname,
                    user.getLocation(),
                    user.getBalance(),
                    subscription,
                    gang,
                    created.format(DateTimeFormatter.ISO_DATE),
                    user.getActiveSettingsConfig() != null ? user.getActiveSettingsConfig().getMessagePackId() : "DEFAULT"
            );
        }
    
    @Override
    public List<ButtonRow> composeButtons(ComposerContext context) {
        var userId = context.get(ContextKey.USER_ID);
        
        var buttons = new ArrayList<ButtonRow>();
        buttons.add(ButtonRow.of(
                Button.builder().callbackData("menu/" + userId + "/user/location").text("📍 Изменить локацию").build()
        ));
        buttons.add(ButtonRow.of(
                Button.builder().callbackData("menu/" + userId + "/reminder").text("🔔 Напоминания").build()
        ));
        buttons.add(ButtonRow.of(
                Button.builder().callbackData("menu/" + userId + "/user/account/1").text("⚙️ Управление аккаунтами").build()
        ));
        
        buttons.add(generateBackButton("menu/" + userId));
        
        return buttons;
    }
}

package dev.knalis.sao_telegram_bot.composer.impl;

import dev.knalis.sao_telegram_bot.composer.ComposerContext;
import dev.knalis.sao_telegram_bot.composer.ContextKey;
import dev.knalis.sao_telegram_bot.composer.intrf.BackComposer;
import dev.knalis.sao_telegram_bot.dto.telegram.Button;
import dev.knalis.sao_telegram_bot.service.intrf.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static dev.knalis.sao_telegram_bot.util.KeyboardUtil.formCallbackButtons;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserMenuComposer implements BackComposer {
    
    UserService userService;

    @Override
    public String composeText(ComposerContext context) {
        String chatIdStr = context.get(ContextKey.CHAT_ID);
            long userId = Long.parseLong(chatIdStr);
            var user = userService.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
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
    public List<List<InlineKeyboardButton>> composeButtons(ComposerContext context) {
        String chatIdStr = context.get(ContextKey.CHAT_ID);
        return formCallbackButtons(
                List.of(Button.builder().callbackData("menu/" + chatIdStr + "/user/location").text("📍 Изменить локацию").build().toInlineButton()),
                List.of(Button.builder().callbackData("menu/" + chatIdStr + "/reminder").text("🔔 Напоминания").build().toInlineButton()),
                List.of(Button.builder().callbackData("menu/" + chatIdStr + "/user/account").text("⚙️ Управление аккаунтами").build().toInlineButton()),
                generateBackButton(context, "menu/" + chatIdStr)
        );
    }
}

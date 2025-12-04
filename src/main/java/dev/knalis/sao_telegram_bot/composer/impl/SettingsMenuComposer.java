package dev.knalis.sao_telegram_bot.composer.impl;

import dev.knalis.sao_telegram_bot.composer.ComposerContext;
import dev.knalis.sao_telegram_bot.composer.ContextKey;
import dev.knalis.sao_telegram_bot.composer.intrf.ListableComposer;
import dev.knalis.sao_telegram_bot.composer.intrf.BackComposer;
import dev.knalis.sao_telegram_bot.dto.Button;
import dev.knalis.sao_telegram_bot.model.user.settings.NotificationSettings;
import dev.knalis.sao_telegram_bot.model.user.settings.NotificationCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static dev.knalis.sao_telegram_bot.util.KeyboardUtil.formCallbackButtons;

@Component
@RequiredArgsConstructor
public class SettingsMenuComposer implements ListableComposer<String>, BackComposer {

    @Override
    public String composeText(ComposerContext context) {
        String category = context.getOrDefault("category", "OTHER");
        return "<b>⚙️ Настройки</b>\n\nКатегория: " + category + "\nНажмите, чтобы переключить.";
    }

    @Override
    public List<List<InlineKeyboardButton>> composeButtons(ComposerContext context) {
        long chatId = Long.parseLong(context.get(ContextKey.CHAT_ID));
        String category = context.getOrDefault("category", "OTHER");

        // Build button rows from enum values for the requested category.
        List<NotificationSettings> items = java.util.Arrays.stream(NotificationSettings.values())
                .filter(n -> n.getCategory().name().equalsIgnoreCase(category) || n.getCategory().name().equalsIgnoreCase(category.toUpperCase()))
                .collect(Collectors.toList());

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (var key : items) {
            String label = key.getVisualName() + " : " + "<b>Вкл</b>"; // state unknown here — show neutral default
            rows.add(List.of(Button.builder()
                    .text(label)
                    .callbackData("settings/" + chatId + "/update/" + category + "/one/" + key.getName())
                    .build().toInlineButton()));
        }

        rows.add(List.of(
                Button.builder().text("Включить все").callbackData("settings/" + chatId + "/update/" + category + "/all/true").build().toInlineButton(),
                Button.builder().text("Выключить все").callbackData("settings/" + chatId + "/update/" + category + "/all/false").build().toInlineButton()
        ));

        rows.add(generateBackButton(context, "message/menu"));
        return rows;
    }
}

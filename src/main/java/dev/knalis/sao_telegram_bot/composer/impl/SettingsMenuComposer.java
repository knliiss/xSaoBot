package dev.knalis.sao_telegram_bot.composer.impl;

import dev.knalis.sao_telegram_bot.composer.ComposerContext;
import dev.knalis.sao_telegram_bot.composer.ContextKey;
import dev.knalis.sao_telegram_bot.composer.intrf.BackComposer;
import dev.knalis.sao_telegram_bot.composer.intrf.ListableComposer;
import dev.knalis.sao_telegram_bot.dto.telegram.Button;
import dev.knalis.sao_telegram_bot.model.user.settings.NotificationCategory;
import dev.knalis.sao_telegram_bot.model.user.settings.NotificationSettings;
import dev.knalis.sao_telegram_bot.service.intrf.UserService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Component
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class SettingsMenuComposer implements ListableComposer<String>, BackComposer {
    
    UserService userService;

    @Override
    public String composeText(ComposerContext context) {
        String category = context.getOrDefault("category", "OTHER");
        return "<b>⚙️ Настройки</b>\n\nКатегория: " + category + "\nНажмите на кнопку, чтобы переключить настройку.";
    }

    @Override
    public List<List<InlineKeyboardButton>> composeButtons(ComposerContext context) {
        long chatId = Long.parseLong(context.get(ContextKey.CHAT_ID));
        String category = context.getOrDefault("category", "OTHER");
        
        List<NotificationSettings> items = Arrays.stream(NotificationSettings.values())
                .filter(n -> n.getCategory().name().equalsIgnoreCase(category) || n.getCategory().name().equalsIgnoreCase(category.toUpperCase()))
                .toList();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        
        boolean isOtherCategory = "OTHER".equalsIgnoreCase(category);
        if (isOtherCategory) {
            List<InlineKeyboardButton> navRow = new ArrayList<>();
            for (NotificationCategory nc : NotificationCategory.values()) {
                if (nc == NotificationCategory.OTHER) continue;
                navRow.add(Button.builder()
                        .text(nc.getVisualName())
                        .callbackData("menu/" + chatId + "/settings/" + nc.name())
                        .build().toInlineButton());
                if (navRow.size() >= 3) {
                    rows.add(new ArrayList<>(navRow));
                    navRow.clear();
                }
            }
            if (!navRow.isEmpty()) rows.add(navRow);
        }
        
        var user = userService.findById(chatId).orElse(null);
        if (isOtherCategory) {
            List<InlineKeyboardButton> currentRow = new ArrayList<>();
            for (var key : items) {
                boolean enabled = false;
                try {
                    if (user != null && user.getActiveSettingsConfig() != null) {
                        enabled = user.getActiveSettingsConfig().getNotifications().stream()
                                .filter(n -> n.getType() == key)
                                .findFirst()
                                .map(n -> n.isEnabled())
                                .orElse(false);
                    }
                } catch (Exception ignored) {
                }
                String stateEmoji = enabled ? "✅" : "❌";
                String label = stateEmoji + " " + key.getVisualName();
                currentRow.add(Button.builder()
                        .text(label)
                        .callbackData("settings/" + chatId + "/" + category + "/update/one/" + key.getName())
                        .build().toInlineButton());
                
                if (currentRow.size() >= 2) {
                    rows.add(new ArrayList<>(currentRow));
                    currentRow.clear();
                }
            }
            if (!currentRow.isEmpty()) rows.add(currentRow);
        } else {
            for (var key : items) {
                boolean enabled = false;
                try { if (user != null && user.getActiveSettingsConfig() != null) enabled = user.getActiveSettingsConfig().getNotifications().stream().filter(n -> n.getType() == key).findFirst().map(n -> n.isEnabled()).orElse(false); } catch (Exception ignored) {}
                String stateEmoji = enabled ? "✅" : "❌";
                String label = stateEmoji + " " + key.getVisualName();
                rows.add(List.of(Button.builder()
                        .text(label)
                        .callbackData("settings/" + chatId + "/" + category + "/update/one/" + key.getName())
                        .build().toInlineButton()));
            }
        }

        rows.add(List.of(
                Button.builder().text("✅ Включить все").callbackData("settings/" + chatId + "/" + category + "/update/all/true").build().toInlineButton(),
                Button.builder().text("🚫 Выключить все").callbackData("settings/" + chatId + "/" + category + "/update/all/false").build().toInlineButton()
        ));
        
        if (isOtherCategory) {
            rows.add(generateBackButton(context, "menu/" + chatId));
        } else {
            rows.add(generateBackButton(context, "menu/" + chatId + "/settings/OTHER"));
        }

        return rows;
    }
}

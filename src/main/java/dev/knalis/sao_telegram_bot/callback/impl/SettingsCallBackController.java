package dev.knalis.sao_telegram_bot.callback.impl;

import dev.knalis.sao_telegram_bot.callback.AbstractCallBackController;
import dev.knalis.sao_telegram_bot.callback.CallBackInfo;
import dev.knalis.sao_telegram_bot.callback.annotation.CallBackController;
import dev.knalis.sao_telegram_bot.callback.annotation.CallBackMethod;
import dev.knalis.sao_telegram_bot.callback.annotation.PathVariable;
import dev.knalis.sao_telegram_bot.model.user.settings.NotificationSettings;
import dev.knalis.sao_telegram_bot.service.crud.impl.ConfigService;
import dev.knalis.sao_telegram_bot.service.crud.impl.SettingsService;
import dev.knalis.sao_telegram_bot.service.telegram.TelegramSenderService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@CallBackController("settings/{userId}")
@Slf4j
public class SettingsCallBackController extends AbstractCallBackController {

    SettingsService settingsService;
    MenuCallBackController menuCallBackController;

    public SettingsCallBackController(TelegramSenderService senderService, SettingsService settingsService, MenuCallBackController menuCallBackController) {
        super(senderService);
        this.settingsService = settingsService;
        this.menuCallBackController = menuCallBackController;
    }

    @CallBackMethod("/update/{category}/all/{state}")
    public void updateAllSettings(@PathVariable("userId") long userId, @PathVariable("category") String category, @PathVariable("state") boolean state, CallBackInfo info) {
        try {
            settingsService.toggleAllSettings(userId, state);
            sendMessage(userId, state ? "✅ Все настройки включены." : "✅ Все настройки выключены.");
        } catch (Exception ex) {
            log.error("Failed to update all settings for user {}: {}", userId, ex.getMessage(), ex);
            sendMessage(userId, "❌ Не удалось обновить настройки: " + ex.getMessage());
        }
        menuCallBackController.settingsMenu(userId, category, info);
    }

  @CallBackMethod("/{category}/update/all/{state}")
    public void updateAllSettingsAlt(@PathVariable("userId") long userId, @PathVariable("category") String category, @PathVariable("state") boolean state, CallBackInfo info) {
        updateAllSettings(userId, category, state, info);
    }

    @CallBackMethod("/update/{category}/one/{type}")
    public void toggleSetting(@PathVariable("userId") long userId, @PathVariable("category") String category, @PathVariable("type") String type, CallBackInfo info) {
        try {
            NotificationSettings ns = NotificationSettings.valueOf(type.toUpperCase());
            settingsService.toggleSetting(userId, ns);
        } catch (IllegalArgumentException ex) {
            log.warn("Unknown notification setting type received: {}", type);
            sendMessage(userId, "⚠️ Неверный тип настройки: " + type);
        } catch (Exception ex) {
            log.error("Failed to toggle setting {} for user {}: {}", type, userId, ex.getMessage(), ex);
            sendMessage(userId, "❌ Ошибка при изменении настройки: " + ex.getMessage());
        }
        menuCallBackController.settingsMenu(userId, category, info);
    }

   @CallBackMethod("/{category}/update/one/{type}")
    public void toggleSettingAlt(@PathVariable("userId") long userId, @PathVariable("category") String category, @PathVariable("type") String type, CallBackInfo info) {
        toggleSetting(userId, category, type, info);
    }
}

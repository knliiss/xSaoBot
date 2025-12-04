package dev.knalis.sao_telegram_bot.callback.impl;

import dev.knalis.sao_telegram_bot.callback.CallBackInfo;
import dev.knalis.sao_telegram_bot.callback.AbstractCallBackController;
import dev.knalis.sao_telegram_bot.callback.annotation.CallBackController;
import dev.knalis.sao_telegram_bot.callback.annotation.CallBackMethod;
import dev.knalis.sao_telegram_bot.callback.annotation.PathVariable;
import dev.knalis.sao_telegram_bot.composer.ComposerContext;
import dev.knalis.sao_telegram_bot.composer.ContextKey;
import dev.knalis.sao_telegram_bot.service.MenuService;
import dev.knalis.sao_telegram_bot.service.telegram.TelegramSenderService;
import dev.knalis.sao_telegram_bot.model.user.settings.NotificationSettings;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@CallBackController("settings/{userId}")
@Slf4j
public class SettingsCallBackController extends AbstractCallBackController {
    
    MenuService menuService;
    
    public SettingsCallBackController(TelegramSenderService senderService, MenuService menuService) {
        super(senderService);
        this.menuService = menuService;
    }
    
    @CallBackMethod("/{category}")
    public void getMenu(@PathVariable("userId") long userId, @PathVariable("category") String category, CallBackInfo info) {
        var context = new ComposerContext(userId);
        context.put("category", category);
        context.put(ContextKey.BACK_CALLBACK_URL, "message/menu");
        var sendMessage = menuService.getSettingsMenu(context);
        editMessage(userId, info.getMessageId(), sendMessage);
    }
    
    @CallBackMethod("/update/{category}/all/{state}")
    public void updateAllSettings(@PathVariable("userId") long userId, @PathVariable("category") String category, @PathVariable("state") boolean state, CallBackInfo info) {
        // Settings update moved in-app — if not implemented here, show friendly message
        sendMessage(userId, "⚠️ Обновление настроек временно недоступно. Попробуйте позже.");
        getMenu(userId, category, info);
    }
    
    @CallBackMethod("/update/{category}/one/{type}")
    public void toggleSetting(@PathVariable("userId") long userId, @PathVariable("category") String category, @PathVariable("type") String type, CallBackInfo info) {
        try {
            NotificationSettings ns = NotificationSettings.valueOf(type.toUpperCase());
            // actual toggle logic moved to SettingsService --- not available here, so inform user
            sendMessage(userId, "⚠️ Переключение настройки временно недоступно. Попробуйте позже.");
        } catch (IllegalArgumentException ex) {
            log.warn("Unknown notification setting type received: {}", type);
            sendMessage(userId, "⚠️ Неверный тип настройки: " + type);
        }
        getMenu(userId, category, info);
    }
}

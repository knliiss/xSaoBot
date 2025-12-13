package dev.knalis.sao_telegram_bot.callback.impl;

import dev.knalis.sao_telegram_bot.callback.AbstractCallBackController;
import dev.knalis.sao_telegram_bot.callback.annotation.CallBackController;
import dev.knalis.sao_telegram_bot.callback.annotation.CallBackMethod;
import dev.knalis.sao_telegram_bot.callback.annotation.PathVariable;
import dev.knalis.sao_telegram_bot.service.intrf.SettingsConfigService;
import dev.knalis.sao_telegram_bot.service.telegram.TelegramSenderService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;


@CallBackController("config/{userId}")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConfigCallBackController extends AbstractCallBackController {
    
    SettingsConfigService configService;
    
    public ConfigCallBackController(TelegramSenderService senderService, SettingsConfigService configService) {
        super(senderService);
        this.configService = configService;
    }
    
    @CallBackMethod("/activate/{configId}")
    public void activate(
            @PathVariable("userId") long userId,
            @PathVariable("configId") long configId) {
        safeExecute(userId, () -> {
            configService.setActiveConfig(userId, configId);
        }, "❌ Не удалось активировать конфиг. Попробуйте позже.");
    }
    
    
}

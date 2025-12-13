package dev.knalis.sao_telegram_bot.service.intrf;

import dev.knalis.sao_telegram_bot.model.user.settings.NotificationSettings;
import dev.knalis.sao_telegram_bot.model.user.settings.SettingsConfig;

import java.util.List;

public interface SettingsConfigService {
    
    SettingsConfig createDefaultConfig(long userId);
    
    List<SettingsConfig> getAllConfigs(long userId);
    
    void setActiveConfig(long userId, Long configId);

    void deleteConfig(long userId, Long configId);

    SettingsConfig updateConfig(SettingsConfig config);
    
    SettingsConfig findById(long userId, Long configId);
    
    void setActiveMessagePack(long userId, String packId);
    
    void toggleNotificationSetting(long userId, Long configId, NotificationSettings notificationSettings);
    void toggleAllNotificationSettings(long userId, Long configId, boolean enabled);
}
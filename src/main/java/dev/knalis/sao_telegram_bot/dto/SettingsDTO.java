package dev.knalis.sao_telegram_bot.dto;

import dev.knalis.sao_telegram_bot.model.user.settings.NotificationSettings;
import lombok.Data;

import java.util.Map;

@Data
public class SettingsDTO {
    private Map<NotificationSettings, Boolean> notificationSettings;
}


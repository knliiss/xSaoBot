package dev.knalis.sao_telegram_bot.model.user.settings;

import dev.knalis.sao_telegram_bot.dto.SettingsDTO;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@Entity
public class Settings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String messagePackId;

    @ElementCollection(fetch = FetchType.LAZY)
    @MapKeyEnumerated(EnumType.STRING)
    @CollectionTable(name = "settings_notifications", joinColumns = @JoinColumn(name = "settings_id"))
    private Map<NotificationSettings, Boolean> notificationSettings = new HashMap<>();

    public Settings() {
        for (NotificationSettings setting : NotificationSettings.values()) {
            notificationSettings.put(setting, true);
        }
        messagePackId = "DEFAULT";
    }

    public Settings(Settings settings) {
        this.messagePackId = settings.messagePackId;
        this.notificationSettings = new HashMap<>(settings.notificationSettings);
        if (messagePackId == null) messagePackId = "DEFAULT";
    }

    public void setEnabled(NotificationSettings type, boolean enabled) {
        notificationSettings.put(type, enabled);
    }

    public void toggle(NotificationSettings type) {
        setEnabled(type, !notificationSettings.get(type));
    }

    public boolean isEnabled(NotificationSettings type) {
        return notificationSettings.getOrDefault(type, false);
    }

    public void setAll(boolean enabled) {
        for (NotificationSettings setting : NotificationSettings.values()) {
            notificationSettings.put(setting, enabled);
        }
    }

    public void copyFrom(SettingsDTO settingsDTO) {
        for (NotificationSettings setting : NotificationSettings.values()) {
            if (settingsDTO.getNotificationSettings().containsKey(setting)) {
                notificationSettings.put(setting, settingsDTO.getNotificationSettings().get(setting));
            }
        }
    }

    public HashMap<NotificationSettings, Boolean> getNotificationSettingsByCategory(NotificationCategory category) {
        HashMap<NotificationSettings, Boolean> settingsByCategory = new HashMap<>();
        for (NotificationSettings setting : NotificationSettings.values()) {
            if (setting.getCategory() == category) {
                settingsByCategory.put(setting, notificationSettings.getOrDefault(setting, false));
            }
        }
        return settingsByCategory;
    }
}

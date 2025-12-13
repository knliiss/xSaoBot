package dev.knalis.sao_telegram_bot.model.user.settings;

import dev.knalis.sao_telegram_bot.model.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Data
@Entity
public class SettingsConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    private String messagePackId;
    
    @OneToMany(mappedBy = "settings", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SettingsNotification> notifications = new ArrayList<>();

    public SettingsConfig() {
        initDefaults();
    }
    
    private void initDefaults() {
        this.name = "default";
        for (NotificationSettings ns : NotificationSettings.values()) {
            SettingsNotification notification = new SettingsNotification();
            notification.setSettings(this);
            notification.setType(ns);
            notification.setEnabled(true);
            notifications.add(notification);
        }
        messagePackId = "DEFAULT";
    }
}

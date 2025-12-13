package dev.knalis.sao_telegram_bot.model.user.settings;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class SettingsNotification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationSettings type;
    
    @Column(nullable = false)
    private boolean enabled;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settings_id", nullable = false)
    private SettingsConfig settings;
}


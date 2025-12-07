package dev.knalis.sao_telegram_bot.model.user.settings;

import dev.knalis.sao_telegram_bot.model.user.User;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class SettingsConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "settings_id")
    private Settings settings;

    public SettingsConfig() {
        this.name = "default";
        this.settings = new Settings();
    }

    public SettingsConfig(String name, Settings settings) {
        this.name = name;
        this.settings = new Settings(settings);
    }
}

package dev.knalis.sao_telegram_bot.model.user.settings;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class SettingsConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

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

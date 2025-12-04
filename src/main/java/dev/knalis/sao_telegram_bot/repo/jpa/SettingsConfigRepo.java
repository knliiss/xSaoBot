package dev.knalis.sao_telegram_bot.repo.jpa;

import dev.knalis.sao_telegram_bot.model.user.settings.SettingsConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingsConfigRepo extends JpaRepository<SettingsConfig, Long> {
}

package dev.knalis.sao_telegram_bot.repo.jpa;

import dev.knalis.sao_telegram_bot.model.user.settings.SettingsConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SettingsConfigRepo extends JpaRepository<SettingsConfig, Long> {
    
    List<SettingsConfig> findByUserId(Long userId);
}

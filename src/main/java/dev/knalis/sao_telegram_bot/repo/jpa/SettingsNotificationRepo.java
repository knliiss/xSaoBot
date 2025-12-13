package dev.knalis.sao_telegram_bot.repo.jpa;

import dev.knalis.sao_telegram_bot.model.user.settings.SettingsNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingsNotificationRepo extends JpaRepository<SettingsNotification, Long> {
}

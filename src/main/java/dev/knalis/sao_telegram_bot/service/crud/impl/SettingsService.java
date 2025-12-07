package dev.knalis.sao_telegram_bot.service.crud.impl;

import dev.knalis.sao_telegram_bot.exception.EntityException;
import dev.knalis.sao_telegram_bot.model.user.User;
import dev.knalis.sao_telegram_bot.model.user.settings.NotificationSettings;
import dev.knalis.sao_telegram_bot.model.user.settings.Settings;
import dev.knalis.sao_telegram_bot.repo.jpa.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SettingsService {

    private final UserRepo userRepo;
    private final ConfigService configService;

    @Transactional
    public void toggleSetting(Long userId, NotificationSettings type) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityException.EntityNotFoundException(String.format("User with id %s not found", userId)));

        configService.getActiveConfig(userId).getSettings().toggle(type);
        userRepo.save(user);
    }

    @Transactional
    public void toggleAllSettings(Long userId, boolean enabled) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityException.EntityNotFoundException(String.format("User with id %s not found", userId)));

        configService.getActiveConfig(userId).getSettings().setAll(enabled);
        userRepo.save(user);
    }

    @Transactional(readOnly = true)
    public boolean isEnabled(Long userId, NotificationSettings type) {
        return configService.getActiveConfig(userId).getSettings().isEnabled(type);
    }

    public List<User> getUsersWithEnabledSetting(NotificationSettings type) {
        return userRepo.findAll().stream()
                .filter(user -> configService.getActiveConfig(user.getId()).getSettings().isEnabled(type))
                .toList();
    }

    public void save(Settings settings) {
        userRepo.saveAll(userRepo.findAll().stream()
                .filter(user -> configService.getActiveConfig(user.getId()).getSettings().getId().equals(settings.getId()))
                .toList());
    }
}

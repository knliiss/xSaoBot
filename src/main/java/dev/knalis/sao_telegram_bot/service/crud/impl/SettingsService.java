package dev.knalis.sao_telegram_bot.service.crud.impl;

import dev.knalis.sao_telegram_bot.dto.SettingsDTO;
import dev.knalis.sao_telegram_bot.exception.EntityException;
import dev.knalis.sao_telegram_bot.model.user.User;
import dev.knalis.sao_telegram_bot.model.user.settings.NotificationSettings;
import dev.knalis.sao_telegram_bot.model.user.settings.Settings;
import dev.knalis.sao_telegram_bot.repo.jpa.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SettingsService {

    private final UserRepo userRepo;
    private final ConfigService configService;

    public void toggleSetting(Long userId, NotificationSettings type) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityException.EntityNotFoundException(String.format("User with id %s not found", userId)));

        configService.getActiveConfig(userId).getSettings().toggle(type);
        userRepo.save(user);
    }

    public void toggleAllSettings(Long userId, boolean enabled) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityException.EntityNotFoundException(String.format("User with id %s not found", userId)));

        configService.getActiveConfig(userId).getSettings().setAll(enabled);
        userRepo.save(user);
    }

    public List<User> getUsersWithEnabledSetting(NotificationSettings type) {
        return userRepo.findAll().stream()
                .filter(user -> configService.getActiveConfig(user.getId()).getSettings().isEnabled(type))
                .toList();
    }

    public void updateAllSettings(Long userId, SettingsDTO settingsDTO) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityException.EntityNotFoundException(String.format("User with id %s not found", userId)));

        configService.getActiveConfig(userId).getSettings().copyFrom(settingsDTO);
        userRepo.save(user);
    }

    public void save(Settings settings) {
        userRepo.saveAll(userRepo.findAll().stream()
                .filter(user -> configService.getActiveConfig(user.getId()).getSettings().getId().equals(settings.getId()))
                .toList());
    }
}

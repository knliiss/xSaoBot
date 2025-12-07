package dev.knalis.sao_telegram_bot.service.crud.impl;

import dev.knalis.sao_telegram_bot.exception.EntityException;
import dev.knalis.sao_telegram_bot.exception.UserException;
import dev.knalis.sao_telegram_bot.model.user.settings.SettingsConfig;
import dev.knalis.sao_telegram_bot.model.user.subscribe.PlanType;
import dev.knalis.sao_telegram_bot.repo.jpa.SettingsConfigRepo;
import dev.knalis.sao_telegram_bot.repo.jpa.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConfigService {

    private final UserRepo userRepo;
    private final SettingsConfigRepo settingsConfigRepo;

    @Transactional
    public List<SettingsConfig> getUserConfigs(Long userId) {
        return settingsConfigRepo.findByUserId(userId);
    }

    @Transactional
    public SettingsConfig getActiveConfig(Long userId) {
        var user = userRepo.findById(userId).orElseThrow(() -> new UserException("User not found"));
        return user.getActiveSettingsConfig();
    }

    public void setActiveConfig(Long userId, Long configId) {
        if (getUserConfigs(userId).stream().noneMatch(config -> config.getId().equals(configId))) {
            throw new UserException("Config with id " + configId + " not found for user " + userId);
        }
        var user = userRepo.findById(userId).orElseThrow(() -> new UserException("User not found"));
        var config = settingsConfigRepo.findById(configId)
                .orElseThrow(() -> new EntityException.EntityNotFoundException("Config with id " + configId + " not found"));
        user.setActiveSettingsConfig(config);
        userRepo.save(user);
    }


    @Transactional
    public void removeConfig(Long userId, Long configId) {
        List<SettingsConfig> configs = getUserConfigs(userId);
        if (configs.size() <= 1) {
            throw new UserException("Cannot remove the only config");
        }
        SettingsConfig toRemove = configs.stream()
                .filter(config -> config.getId().equals(configId))
                .findFirst()
                .orElseThrow(() -> new EntityException.EntityNotFoundException("Config with id " + configId + " not found for user " + userId));
        if (toRemove.getId().equals(getActiveConfig(userId).getId())) {
            SettingsConfig newActive = configs.stream()
                    .filter(config -> !config.getId().equals(configId))
                    .findFirst()
                    .orElseThrow();
            setActiveConfig(userId, newActive.getId());
        }
        settingsConfigRepo.delete(toRemove);
    }

    @Transactional
    public SettingsConfig createConfig(Long userId, String name) {
        var user = userRepo.findById(userId).orElseThrow(() -> new UserException("User not found"));

        int configsLimit = user.getSubscription().getPlan() == PlanType.FREE ? 2 : 5;
        List<SettingsConfig> configs = getUserConfigs(userId);
        if (configs.size() >= configsLimit) {
            throw new UserException("Config limit exceeded");
        }

        var activeConfig = getActiveConfig(userId);
        var newConfig = new SettingsConfig(name, activeConfig.getSettings());
        newConfig = settingsConfigRepo.save(newConfig);

        user.getSettingsConfigs().add(newConfig);
        userRepo.save(user);

        setActiveConfig(userId, newConfig.getId());
        return newConfig;
    }

}

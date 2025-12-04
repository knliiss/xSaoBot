package dev.knalis.sao_telegram_bot.service.crud.impl;

import dev.knalis.sao_telegram_bot.exception.EntityException;
import dev.knalis.sao_telegram_bot.exception.UserException;
import dev.knalis.sao_telegram_bot.model.user.User;
import dev.knalis.sao_telegram_bot.model.user.settings.SettingsConfig;
import dev.knalis.sao_telegram_bot.model.user.subscribe.PlanType;
import dev.knalis.sao_telegram_bot.repo.jpa.SettingsConfigRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConfigService {

    private final UserService userService;
    private final SettingsConfigRepo settingsConfigRepo;

    public List<SettingsConfig> getUserConfigs(Long userId) {
        return userService.findById(userId).getSettingsConfigs();
    }

    public SettingsConfig getActiveConfig(Long userId) {
        User user = userService.findById(userId);
        return getUserConfigs(userId)
                .stream()
                .filter(config -> config.getId().equals(user.getConfigId()))
                .findFirst()
                .orElseThrow();
    }

    public void setActiveConfig(Long userId, Long configId) {
        User user = userService.findById(userId);
        if (getUserConfigs(userId).stream().noneMatch(config -> config.getId().equals(configId))) {
            throw new UserException("Config with id " + configId + " not found for user " + userId);
        }
        user.setConfigId(configId);
        userService.update(user);
    }

    public void activateConfig(Long userId, Long configId) { setActiveConfig(userId, configId); }

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
        configs.remove(toRemove);
        userService.update(userService.findById(userId));
    }

    @Transactional
    public SettingsConfig createConfig(Long userId, String name) {
        var user = userService.findById(userId);
        var configs = user.getSettingsConfigs();
        
        int configsLimit = user.getSubscription().getPlan().equals(PlanType.FREE) ? 2 : 5;
        if (configs.size() >= configsLimit) {
            throw new UserException("Config limit exceeded");
        }
        
        var activeConfig = getActiveConfig(userId);
        var newConfig = new SettingsConfig(name, activeConfig.getSettings());
        newConfig = settingsConfigRepo.save(newConfig);
        userService.update(user);
        setActiveConfig(userId, newConfig.getId());
        return newConfig;
    }


}

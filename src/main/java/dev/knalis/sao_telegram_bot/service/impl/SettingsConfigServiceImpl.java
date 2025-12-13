package dev.knalis.sao_telegram_bot.service.impl;

import dev.knalis.sao_telegram_bot.model.user.User;
import dev.knalis.sao_telegram_bot.model.user.settings.NotificationSettings;
import dev.knalis.sao_telegram_bot.model.user.settings.SettingsConfig;
import dev.knalis.sao_telegram_bot.repo.jpa.SettingsConfigRepo;
import dev.knalis.sao_telegram_bot.repo.jpa.UserRepo;
import dev.knalis.sao_telegram_bot.service.intrf.SettingsConfigService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SettingsConfigServiceImpl implements SettingsConfigService {
    
    SettingsConfigRepo configRepository;
    UserRepo userRepository;
    
    @Override
    @Transactional
    public SettingsConfig createDefaultConfig(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        SettingsConfig config = new SettingsConfig();
        config.setUser(user);
        user.getSettingsConfigs().add(config);
        configRepository.save(config);
        if (user.getActiveSettingsConfig() == null) {
            user.setActiveSettingsConfig(config);
        }
        userRepository.save(user);
        return config;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SettingsConfig> getAllConfigs(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return new ArrayList<>(user.getSettingsConfigs());
    }
    
    @Override
    @Transactional
    public void setActiveConfig(long userId, Long configId) {
        SettingsConfig config = findById(userId, configId);
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setActiveSettingsConfig(config);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteConfig(long userId, Long configId) {
        SettingsConfig config = findById(userId, configId);
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (user.getActiveSettingsConfig() != null && user.getActiveSettingsConfig().getId().equals(configId)) {
            user.setActiveSettingsConfig(null);
        }
        user.getSettingsConfigs().remove(config);
        configRepository.delete(config);
        userRepository.save(user);
    }
    
    @Override
    @Transactional
    public SettingsConfig updateConfig(SettingsConfig config) {
        return configRepository.save(config);
    }
    
    @Override
    @Transactional(readOnly = true)
    public SettingsConfig findById(long userId, Long configId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return user.getSettingsConfigs().stream()
                .filter(c -> c.getId().equals(configId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Config not found for user"));
    }
    
    @Override
    @Transactional
    public void setActiveMessagePack(long userId, String packId) {
        SettingsConfig activeConfig = userRepository.findById(userId).orElseThrow(() -> new IllegalStateException("User not found")).getActiveSettingsConfig();
        if (activeConfig == null) {
            throw new IllegalStateException("User has no active settings config");
        }
        activeConfig.setMessagePackId(packId);
        configRepository.save(activeConfig);
    }
    
    @Override
    @Transactional
    public void toggleNotificationSetting(long userId, Long configId, NotificationSettings type) {
        var config = findById(userId, configId);
        config.getNotifications().stream()
                .filter(n -> n.getType() == type)
                .findFirst()
                .ifPresent(n -> n.setEnabled(!n.isEnabled()));
        configRepository.save(config);
    }
    
    @Override
    @Transactional
    public void toggleAllNotificationSettings(long userId, Long configId, boolean enabled) {
        var config = findById(userId, configId);
        config.getNotifications().forEach(n -> n.setEnabled(enabled));
        configRepository.save(config);
    }
}
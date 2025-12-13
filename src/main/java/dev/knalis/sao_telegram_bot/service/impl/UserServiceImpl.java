package dev.knalis.sao_telegram_bot.service.impl;

import dev.knalis.sao_telegram_bot.dto.entity.UserCreateDTO;
import dev.knalis.sao_telegram_bot.exception.EntityException;
import dev.knalis.sao_telegram_bot.model.user.Role;
import dev.knalis.sao_telegram_bot.model.user.User;
import dev.knalis.sao_telegram_bot.model.user.settings.SettingsConfig;
import dev.knalis.sao_telegram_bot.repo.jpa.UserRepo;
import dev.knalis.sao_telegram_bot.service.intrf.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    
    UserRepo userRepo;
    
    @Override
    @Transactional
    public User create(UserCreateDTO userCreateDTO) {
        var user = new User();
        user.setUsername(userCreateDTO.getUsername());
        user.setFirstName(userCreateDTO.getFirstName());
        user.setLastName(userCreateDTO.getLastName());
        user.setId(userCreateDTO.getId());
        user.getRoles().add(Role.USER);
        
        var settingsConfig = new SettingsConfig();
        settingsConfig.setUser(user);
        
        user.getSettingsConfigs().add(settingsConfig);
        user.setActiveSettingsConfig(settingsConfig);
        return userRepo.save(user);
    }
    
    @Override
    public void updateLocationByUserId(long userId, short location) {
        var user = userRepo.findById(userId).orElseThrow(() -> new EntityException("User not found"));
        user.setLocation(location);
        userRepo.save(user);
    }
    
    @Override
    public boolean updateNickNameByUserId(long userId, String nickName) {
        var user = userRepo.findById(userId).orElseThrow(() -> new EntityException("User not found"));
        if (existsByNickName(nickName)) {
            throw new EntityException("Nickname already exists");
        }
        
        if (nickName.length() <= 3 || nickName.length() > 15) {
            throw new IllegalArgumentException("Nickname too long. Length must be between 3 and 15");
        }
        
        user.setNickname(nickName);
        userRepo.save(user);
        return true;
    }
    
    @Override
    @Transactional
    public void linkAdditionalAccount(long userId, String additionalAccount) {
        var user = userRepo.findById(userId).orElseThrow(() -> new EntityException("User not found"));
        user.getAdditionalAccounts().add(additionalAccount);
        userRepo.save(user);
    }
    
    @Override
    @Transactional
    public void unlinkAdditionalAccount(long userId, String additionalAccount) {
        var user = userRepo.findById(userId).orElseThrow(() -> new EntityException("User not found"));
        user.getAdditionalAccounts().remove(additionalAccount);
        userRepo.save(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<String> getAdditionalAccounts(long userId) {
        var user = userRepo.findById(userId).orElseThrow(() -> new EntityException("User not found"));
        return new ArrayList<>(user.getAdditionalAccounts());
    }
    
    @Override
    @Transactional(readOnly = true)
    public SettingsConfig getActiveSettingsConfig(long userId) {
        var user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityException("User not found"));
        var cfg = user.getActiveSettingsConfig();
        if (cfg != null) {
            cfg.getNotifications().size();
        }
        return cfg;
    }
    
    @Override
    public Optional<User> findByNickName(String nickName) {
        return userRepo.findByNickname(nickName);
    }
    
    @Override
    public Optional<User> findById(long id) {
        return userRepo.findById(id);
    }
    
    @Override
    public List<User> findAllWithAdditionalAccount(String additionalAccount) {
        return userRepo.findAllByAdditionalAccountsContainingIgnoreCase(additionalAccount);
    }
    
    @Override
    public List<User> findAllFromList(List<Long> ids) {
        return userRepo.findAllById(ids);
    }
    
    @Override
    public List<User> findAll() {
        return userRepo.findAll();
    }
    
    @Override
    public boolean existsByNickName(String nickName) {
        return userRepo.existsByNickname(nickName);
    }
    
    @Override
    public void withdrawBalance(long userId, double amount) {
        var user = userRepo.findById(userId).orElseThrow(() -> new EntityException("User not found"));
        var balance = user.getBalance();
        if (balance < amount) {
            throw new IllegalArgumentException("Not enough balance");
        }
        user.setBalance(balance - amount);
        userRepo.save(user);
    }
    
    @Override
    public void addBalance(long userId, double amount) {
        var user = userRepo.findById(userId).orElseThrow(() -> new EntityException("User not found"));
        var balance = user.getBalance();
        user.setBalance(balance + amount);
        userRepo.save(user);
    }
}

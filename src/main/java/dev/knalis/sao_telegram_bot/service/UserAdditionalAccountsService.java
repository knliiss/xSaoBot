package dev.knalis.sao_telegram_bot.service;

import dev.knalis.sao_telegram_bot.model.user.User;
import dev.knalis.sao_telegram_bot.repo.jpa.UserRepo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserAdditionalAccountsService {

    UserRepo userRepo;
    
    public List<String> getAdditionalAccounts(Long userId) {
        User user = userRepo.findById(userId).orElseThrow(() -> new NullPointerException("User not found"));
        return user.getAdditionalAccounts();
    }
    
    public List<String> addAdditionalAccount(Long userId, String account) {
        User user = userRepo.findById(userId).orElseThrow(() -> new NullPointerException("User not found"));
        List<String> accounts = user.getAdditionalAccounts();
        if (!accounts.contains(account)) {
            accounts.add(account);
            user.setAdditionalAccounts(accounts);
            userRepo.save(user);
        }
        return accounts;
    }
    
    public List<String> removeAdditionalAccount(Long userId, String account) {
        User user = userRepo.findById(userId).orElseThrow(() -> new NullPointerException("User not found"));
        List<String> accounts = user.getAdditionalAccounts();
        if (accounts.contains(account)) {
            accounts.remove(account);
            user.setAdditionalAccounts(accounts);
            userRepo.save(user);
        }
        return accounts;
    }
    
    public List<User> getUsersByAdditionalAccount(String account) {
        return userRepo.findAll().stream()
                .filter(user -> user.getAdditionalAccounts().contains(account))
                .toList();
    }
    
}

package dev.knalis.sao_telegram_bot.service;

import dev.knalis.sao_telegram_bot.exception.BalanceException;
import dev.knalis.sao_telegram_bot.exception.EntityException;
import dev.knalis.sao_telegram_bot.model.user.User;
import dev.knalis.sao_telegram_bot.repo.jpa.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BalanceService {

    private final UserRepo userRepo;

    public void changeBalance(Long userId, Double amount) {
        userRepo.findById(userId).ifPresent(user -> {
            Double newBalance = user.getBalance() + amount;
            if (newBalance < 0) {
                newBalance = 0.0;
            }
            user.setBalance(newBalance);
        });
    }

    public void withdraw(Long userId, Double amount) {
        User user = userRepo.findById(userId).orElseThrow(() -> new EntityException.EntityNotFoundException(String.format("User with id %s not found", userId)));
        Double newBalance = user.getBalance() - amount;
        if (newBalance < 0) {
            throw new BalanceException(String.format("User with id %s does not have enough balance", userId));
        }
        user.setBalance(newBalance);
    }

    public void addBalanceToAll(Double amount) {
        if (amount < 0) {
            throw new BalanceException("Amount must be positive");
        }
        userRepo.findAll().forEach(user -> user.setBalance(user.getBalance() + amount));
    }
}

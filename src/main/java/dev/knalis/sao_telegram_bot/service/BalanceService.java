package dev.knalis.sao_telegram_bot.service;

public interface BalanceService {
    void withdraw(Long userId, Double amount);
}


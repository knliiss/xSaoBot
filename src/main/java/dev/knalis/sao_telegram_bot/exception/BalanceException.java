package dev.knalis.sao_telegram_bot.exception;

public class BalanceException extends RuntimeException {
    public BalanceException(String message) {
        super(message);
    }
}

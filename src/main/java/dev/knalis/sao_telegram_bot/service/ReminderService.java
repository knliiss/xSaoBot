package dev.knalis.sao_telegram_bot.service;

import java.util.List;

public interface ReminderService {
    List<String> getReminders(Long userId);
}


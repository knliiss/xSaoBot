package dev.knalis.sao_telegram_bot.dto;

import java.time.Instant;
import lombok.Data;

@Data
public class ScheduledMessageUpdateRequest {
    private String content;
    private Instant scheduledTime;
}


package dev.knalis.sao_telegram_bot.dto;

import lombok.Data;

@Data
public class AllowResponse {
    private boolean allowed;
    private String reason;
}

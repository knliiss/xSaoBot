package dev.knalis.sao_telegram_bot.dto;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String nickname;
    private Short location;
}

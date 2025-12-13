package dev.knalis.sao_telegram_bot.dto.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCreateDTO {
    private String username;
    private String firstName;
    private String lastName;
    
    private long id;
}

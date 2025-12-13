package dev.knalis.sao_telegram_bot.dto.entity;

import dev.knalis.sao_telegram_bot.model.user.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GangCreateDTO {
    private String name;
    private User createdBy;
}

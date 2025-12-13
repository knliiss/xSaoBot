package dev.knalis.sao_telegram_bot.dto.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GangDeleteDTO {
    private long gangId;
    private long actorId;
}

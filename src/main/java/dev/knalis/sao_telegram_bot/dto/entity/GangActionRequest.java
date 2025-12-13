package dev.knalis.sao_telegram_bot.dto.entity;

import lombok.Data;

@Data
public class GangActionRequest {
    private long gangId;
    private long actorId;
    private long targetId;
    private GangActionType actionType;
}

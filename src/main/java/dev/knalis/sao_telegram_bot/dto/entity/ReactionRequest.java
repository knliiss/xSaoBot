package dev.knalis.sao_telegram_bot.dto.entity;

import dev.knalis.sao_telegram_bot.model.ReactionType;
import lombok.Data;

@Data
public class ReactionRequest {
    private long userId;
    private long ideaId;
    private ReactionType reactionType;
}

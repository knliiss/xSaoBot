package dev.knalis.sao_telegram_bot.dto.entity;

import dev.knalis.sao_telegram_bot.model.user.User;
import lombok.Data;

@Data
public class IdeaCreateRequest {
    private String title;
    private String content;
    private long authorId;
}

package dev.knalis.sao_telegram_bot.dto;

import lombok.Data;

@Data
public class IdeaCreateRequest {
    private long authorId;
    private String content;
}


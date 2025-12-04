package dev.knalis.sao_telegram_bot.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class MessageRequest implements Serializable {
    private Long chatId;
    private String text;


}
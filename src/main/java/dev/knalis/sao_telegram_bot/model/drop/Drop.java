package dev.knalis.sao_telegram_bot.model.drop;

import lombok.Data;

import java.time.Instant;

@Data
public class Drop {

    private String username;
    private String item;
    private Instant date;

    public Drop(String username) {
        this.username = username;
        this.date = Instant.now();
    }

}

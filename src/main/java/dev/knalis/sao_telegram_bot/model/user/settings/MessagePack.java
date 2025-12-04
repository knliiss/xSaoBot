package dev.knalis.sao_telegram_bot.model.user.settings;

import dev.knalis.sao_telegram_bot.model.drop.Rarity;
import lombok.Data;

import java.util.HashMap;

@Data
public class MessagePack {

    private String id;
    private String name;
    private String emoji;
    private HashMap<String, String> messages;
    private Double cost;
    private Rarity rarity;

}
package dev.knalis.sao_telegram_bot.service.intrf;

import dev.knalis.sao_telegram_bot.model.drop.Rarity;
import dev.knalis.sao_telegram_bot.model.user.settings.MessagePack;

import java.util.List;

public interface MessagePackService {
    MessagePack getById(String id);
    List<MessagePack> getByRarity(Rarity rarity);
    
    List<MessagePack> getAll();
    String getMessage(String packId, String key);
    MessagePack buyMessagePack(String packId, long userId);
}

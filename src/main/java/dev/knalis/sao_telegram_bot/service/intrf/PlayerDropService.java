package dev.knalis.sao_telegram_bot.service.intrf;

import dev.knalis.sao_telegram_bot.model.drop.Drop;
import dev.knalis.sao_telegram_bot.model.drop.PlayerDrops;

import java.util.List;

public interface PlayerDropService {
    PlayerDrops save(Drop drop);
    
    PlayerDrops findByNickName(String nickName);
    List<PlayerDrops> findAll();
    
    void deleteByNickNameAndItemName(String nickName, String itemName);
    List<Drop> findDropsByDropName(String dropName);
    
}

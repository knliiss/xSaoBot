package dev.knalis.sao_telegram_bot.service.impl;

import dev.knalis.sao_telegram_bot.model.drop.Drop;
import dev.knalis.sao_telegram_bot.model.drop.PlayerDrops;
import dev.knalis.sao_telegram_bot.repo.mongo.PlayerDropRepo;
import dev.knalis.sao_telegram_bot.service.intrf.PlayerDropService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PlayerDropServiceImpl implements PlayerDropService {
    
    PlayerDropRepo playerDropRepo;
    
    @Override
    public PlayerDrops save(Drop drop) {
        String username = drop.getUsername();
        PlayerDrops playerDrops = playerDropRepo.findById(username)
                .orElseGet(() -> {
                    PlayerDrops newPd = new PlayerDrops();
                    newPd.setUsername(username);
                    return newPd;
                });
        playerDrops.getDrops().add(drop);
        return playerDropRepo.save(playerDrops);
    }
    
    @Override
    public PlayerDrops findByNickName(String nickName) {
        return playerDropRepo.findById(nickName).orElseThrow(() -> new IllegalArgumentException("PlayerDrops not found for nickname: " + nickName));
    }
    
    @Override
    public List<PlayerDrops> findAll() {
        return playerDropRepo.findAll();
    }
    
    @Override
    public void deleteByNickNameAndItemName(String nickName, String itemName) {
        PlayerDrops playerDrops = findByNickName(nickName);
        playerDrops.getDrops().removeIf(drop -> drop.getItem().equals(itemName));
        playerDropRepo.save(playerDrops);
    }
    
    @Override
    public List<Drop> findDropsByDropName(String dropName) {
        var list = playerDropRepo.findAll();
        return list.stream().filter(pd -> pd.getDrops().stream().anyMatch(drop -> drop.getItem().toLowerCase().contains(dropName.toLowerCase())))
                .flatMap(pd -> pd.getDrops().stream().filter(drop -> drop.getItem().toLowerCase().contains(dropName.toLowerCase())))
                .toList();
    }
}

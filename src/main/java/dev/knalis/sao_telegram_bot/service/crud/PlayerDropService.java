package dev.knalis.sao_telegram_bot.service.crud;

import dev.knalis.sao_telegram_bot.exception.EntityException;
import dev.knalis.sao_telegram_bot.model.PlayerDrops;
import dev.knalis.sao_telegram_bot.model.drop.Drop;
import dev.knalis.sao_telegram_bot.repo.mongo.PlayerDropRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class PlayerDropService {

    private final PlayerDropRepo playerDropRepo;

    public PlayerDrops saveDrop(Drop drop) {
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

    public List<PlayerDrops> findAll() {
        return playerDropRepo.findAll();
    }

    public PlayerDrops findByUsername(String username) {
        return playerDropRepo.findById(username)
                .orElseThrow(() -> new EntityException.EntityNotFoundException(
                        String.format("PlayerDrops with username '%s' not found", username)
                ));
    }

    public boolean existsByUsername(String username) {
        return playerDropRepo.existsById(username);
    }

}

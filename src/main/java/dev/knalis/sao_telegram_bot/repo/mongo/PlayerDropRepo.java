package dev.knalis.sao_telegram_bot.repo.mongo;

import dev.knalis.sao_telegram_bot.model.drop.PlayerDrops;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PlayerDropRepo extends MongoRepository<PlayerDrops, String> {
}

package dev.knalis.sao_telegram_bot.repo.mongo;

import dev.knalis.sao_telegram_bot.model.event.Event;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface EventRepo extends MongoRepository<Event, String> {
    Event findTopByOrderByTimestampDesc();
}

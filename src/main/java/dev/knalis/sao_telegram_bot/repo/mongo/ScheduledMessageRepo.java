package dev.knalis.sao_telegram_bot.repo.mongo;

import dev.knalis.sao_telegram_bot.model.ScheduledMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ScheduledMessageRepo extends MongoRepository<ScheduledMessage, String> {
}

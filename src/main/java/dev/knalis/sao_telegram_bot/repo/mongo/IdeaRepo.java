package dev.knalis.sao_telegram_bot.repo.mongo;

import dev.knalis.sao_telegram_bot.model.Idea;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IdeaRepo extends MongoRepository<Idea, String> {

}

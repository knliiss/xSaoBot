package dev.knalis.sao_telegram_bot.repo.jpa;

import dev.knalis.sao_telegram_bot.model.Idea;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdeaRepo extends JpaRepository<Idea, Long> {

}

package dev.knalis.sao_telegram_bot.repo.jpa;

import dev.knalis.sao_telegram_bot.model.IdeaReaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IdeaReactionRepo extends JpaRepository<IdeaReaction, Long> {
    Optional<IdeaReaction> findByIdea_IdAndUser_Id(Long ideaId, long userId);
    
    List<IdeaReaction> findAllByIdea_Id(Long ideaId);
    
    List<IdeaReaction> findAllByUser_Id(long userId);
}

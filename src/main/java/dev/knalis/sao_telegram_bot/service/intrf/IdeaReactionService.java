package dev.knalis.sao_telegram_bot.service.intrf;

import dev.knalis.sao_telegram_bot.dto.entity.ReactionRequest;
import dev.knalis.sao_telegram_bot.model.IdeaReaction;

import java.util.List;
import java.util.Optional;

public interface IdeaReactionService {

    List<IdeaReaction> findAll();
    Optional<IdeaReaction> findById(long id);
    
    IdeaReaction save(ReactionRequest reactionRequest);
    void delete(long id);
    
    List<IdeaReaction> findByIdeaId(long ideaId);
    List<IdeaReaction> findByUserId(long userId);
    
    Optional<IdeaReaction> findByIdeaIdAndUserId(long ideaId, long userId);
    
}

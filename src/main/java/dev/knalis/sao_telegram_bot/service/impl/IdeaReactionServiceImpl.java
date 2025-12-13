package dev.knalis.sao_telegram_bot.service.impl;

import dev.knalis.sao_telegram_bot.dto.entity.ReactionRequest;
import dev.knalis.sao_telegram_bot.model.IdeaReaction;
import dev.knalis.sao_telegram_bot.repo.jpa.IdeaReactionRepo;
import dev.knalis.sao_telegram_bot.service.intrf.IdeaReactionService;
import dev.knalis.sao_telegram_bot.service.intrf.IdeaService;
import dev.knalis.sao_telegram_bot.service.intrf.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IdeaReactionServiceImpl implements IdeaReactionService {
    
    IdeaReactionRepo ideaReactionRepo;
    private final UserService userService;
    private final IdeaService ideaService;
    
    @Override
    public List<IdeaReaction> findAll() {
        return ideaReactionRepo.findAll();
    }
    
    @Override
    public Optional<IdeaReaction> findById(long id) {
        return ideaReactionRepo.findById(id);
    }
    
    @Override
    public IdeaReaction save(ReactionRequest reactionRequest) {
        var user = userService.findById(reactionRequest.getUserId()).orElseThrow();
        var idea = ideaService.findById(reactionRequest.getIdeaId()).orElseThrow();
        var reactionOptional = findByIdeaIdAndUserId(idea.getId(), user.getId());
        var reaction = reactionOptional.orElse(null);
        if (reaction == null) {
            reaction = new IdeaReaction();
            reaction.setUser(user);
            reaction.setIdea(idea);
        }
        reaction.setReactionType(reactionRequest.getReactionType());
        return ideaReactionRepo.save(reaction);
    }
    
    @Override
    public void delete(long id) {
        ideaReactionRepo.deleteById(id);
    }
    
    @Override
    public List<IdeaReaction> findByIdeaId(long ideaId) {
        return ideaReactionRepo.findAllByIdea_Id(ideaId);
    }
    
    @Override
    public List<IdeaReaction> findByUserId(long userId) {
        return ideaReactionRepo.findAllByUser_Id(userId);
    }
    
    @Override
    public Optional<IdeaReaction> findByIdeaIdAndUserId(long ideaId, long userId) {
        return ideaReactionRepo.findByIdea_IdAndUser_Id(ideaId, userId);
    }
}

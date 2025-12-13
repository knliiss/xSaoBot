package dev.knalis.sao_telegram_bot.service.intrf;

import dev.knalis.sao_telegram_bot.dto.entity.IdeaCreateRequest;
import dev.knalis.sao_telegram_bot.model.Idea;
import dev.knalis.sao_telegram_bot.model.IdeaStatus;

import java.util.List;
import java.util.Optional;

public interface IdeaService {
    
    Optional<Idea> findById(long id);
    List<Idea> findAll();
    
    void delete(long id);
    Idea create(IdeaCreateRequest request);
    boolean updateStatus(long id, IdeaStatus status);
    
    boolean canDelete(long id, long userId);
}

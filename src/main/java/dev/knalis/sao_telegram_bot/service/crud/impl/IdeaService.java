package dev.knalis.sao_telegram_bot.service.crud.impl;

import dev.knalis.sao_telegram_bot.dto.IdeaCreateRequest;
import dev.knalis.sao_telegram_bot.model.Idea;
import dev.knalis.sao_telegram_bot.model.IdeaStatus;
import dev.knalis.sao_telegram_bot.repo.mongo.IdeaRepo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IdeaService {

    IdeaRepo ideaRepo;

    public Idea saveIdea(IdeaCreateRequest request) {
        long authorId = request.getAuthorId();
        String content = request.getContent();
        var idea = new Idea();
        idea.setAuthorId(authorId);
        idea.setContent(content);
        idea.setStatus(IdeaStatus.PENDING);
        return ideaRepo.save(idea);
    }

    public List<Idea> getAllIdeas() {
        return ideaRepo.findAll();
    }

    public void filterIdeasByStatus(List<Idea> ideas, IdeaStatus status) {
        ideas.removeIf(idea -> idea.getStatus() != status);
    }

    public List<Idea> getIdeasByAuthorId(long authorId) {
        return ideaRepo.findAll().stream().filter(idea -> idea.getAuthorId() == authorId).toList();
    }

    public void updateIdeaStatus(String ideaId, IdeaStatus newStatus) {
        var ideaOpt = ideaRepo.findById(ideaId);
        if (ideaOpt.isPresent()) {
            var idea = ideaOpt.get();
            idea.setStatus(newStatus);
            ideaRepo.save(idea);
        }
    }

    public void deleteIdea(String ideaId) {
        ideaRepo.deleteById(ideaId);
    }

}

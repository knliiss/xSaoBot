package dev.knalis.sao_telegram_bot.service.impl;

import dev.knalis.sao_telegram_bot.dto.entity.IdeaCreateRequest;
import dev.knalis.sao_telegram_bot.model.Idea;
import dev.knalis.sao_telegram_bot.model.IdeaStatus;
import dev.knalis.sao_telegram_bot.model.user.Role;
import dev.knalis.sao_telegram_bot.repo.jpa.IdeaRepo;
import dev.knalis.sao_telegram_bot.service.intrf.IdeaService;
import dev.knalis.sao_telegram_bot.service.intrf.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IdeaServiceImpl implements IdeaService {
    
    IdeaRepo ideaRepo;
    UserService userService;
    
    @Override
    public Optional<Idea> findById(long id) {
        return ideaRepo.findById(id);
    }
    
    @Override
    public List<Idea> findAll() {
        return ideaRepo.findAll();
    }
    
    @Override
    public void delete(long id) {
        var idea = ideaRepo.findById(id).orElseThrow(() -> new IllegalArgumentException(String.format("Idea with id %d not found", id)));
        ideaRepo.delete(idea);
    }
    
    @Override
    @Transactional
    public Idea create(IdeaCreateRequest request) {
        var authorId = request.getAuthorId();
        var author = userService.findById(authorId).orElseThrow(() -> new IllegalArgumentException(String.format("User with id %d not found", authorId)));
        var idea = new Idea();
        idea.setContent(request.getContent());
        idea.setTitle(request.getTitle());
        idea.setAuthor(author);
        return ideaRepo.save(idea);
    }
    
    @Override
    @Transactional
    public boolean updateStatus(long id, IdeaStatus status) {
        var idea = findById(id).orElseThrow(() -> new IllegalArgumentException(String.format("Idea with id %d not found", id)));
        if (idea.getStatus() == status || idea.getStatus() == IdeaStatus.COMPLETED) {
            return false;
        }
        
        if (status == IdeaStatus.COMPLETED) {
            var author = idea.getAuthor();
            userService.addBalance(author.getId(), 50);
        }
        
        idea.setStatus(status);
        ideaRepo.save(idea);
        return true;
    }
    
    @Override
    public boolean canDelete(long id, long userId) {
        var idea = findById(id).orElseThrow(() -> new IllegalArgumentException(String.format("Idea with id %d not found", id)));
        var user = userService.findById(userId).orElseThrow(() -> new IllegalArgumentException(String.format("User with id %d not found", userId)));
        return (idea.getAuthor() != null && idea.getAuthor().getId() == userId && idea.getStatus() == IdeaStatus.PENDING) || user.getRoles().contains(Role.ADMIN);
    }
}

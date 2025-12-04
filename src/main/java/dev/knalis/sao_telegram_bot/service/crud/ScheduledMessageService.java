package dev.knalis.sao_telegram_bot.service.crud;

import dev.knalis.sao_telegram_bot.dto.ScheduledMessageUpdateRequest;
import dev.knalis.sao_telegram_bot.exception.EntityException;
import dev.knalis.sao_telegram_bot.model.ScheduledMessage;
import dev.knalis.sao_telegram_bot.repo.mongo.ScheduledMessageRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduledMessageService {

    private final ScheduledMessageRepo repo;

    public List<ScheduledMessage> findAll() {
        return repo.findAll();
    }

    public List<ScheduledMessage> findDueMessages(Long userId) {
        return repo.findAll().stream().filter(msg -> msg.getUserId().equals(userId)).toList();
    }

    public ScheduledMessage create(ScheduledMessage message) {
        return repo.save(message);
    }

    public ScheduledMessage update(String id, ScheduledMessageUpdateRequest request) {
        ScheduledMessage message = repo.findById(id).orElseThrow(() -> new EntityException.EntityNotFoundException("Scheduled message not found"));
        if (request.getContent() != null) {
            message.setMessage(request.getContent());
        }
        if (request.getScheduledTime() != null) {
            message.setScheduledTime(request.getScheduledTime());
        }
        return repo.save(message);
    }

    public void deleteById(String id) {
        repo.deleteById(id);
    }




}

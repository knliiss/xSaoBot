package dev.knalis.sao_telegram_bot.service.impl;

import dev.knalis.sao_telegram_bot.model.event.Event;
import dev.knalis.sao_telegram_bot.model.event.EventType;
import dev.knalis.sao_telegram_bot.repo.mongo.EventRepo;
import dev.knalis.sao_telegram_bot.service.intrf.EventService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class EventServiceImpl implements EventService {
    
    EventRepo eventRepo;
    
    @Override
    public Event getLatestEvent() {
        return eventRepo.findTopByOrderByTimestampDesc();
    }
    
    @Override
    public Event save(EventType eventType) {
        return eventRepo.save(new Event(eventType));
    }
    
    @Override
    public List<Event> findAll() {
        return eventRepo.findAll();
    }
    
    @Override
    public List<Event> findByEventType(EventType eventType) {
        return eventRepo.findAll().stream()
                .filter(event -> event.getEventType() == eventType)
                .toList();
    }
    
    @Override
    public void delete(String id) {
        eventRepo.deleteById(id);
    }
}

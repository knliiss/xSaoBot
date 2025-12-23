package dev.knalis.sao_telegram_bot.service.intrf;

import dev.knalis.sao_telegram_bot.model.event.Event;
import dev.knalis.sao_telegram_bot.model.event.EventType;

import java.util.List;

public interface EventService {
    Event getLatestEvent();
    Event save(EventType eventType);
    
    List<Event> findAll();
    List<Event> findByEventType(EventType eventType);
    
    void delete(String id);
    
}

package dev.knalis.sao_telegram_bot.model.event;

import jakarta.persistence.Id;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Document
public class Event {
    
    @Id
    private String id;
    private final EventType eventType;
    private final Instant timestamp = Instant.now();
    
    public Event(EventType eventType) {
        this.eventType = eventType;
    }
    
}

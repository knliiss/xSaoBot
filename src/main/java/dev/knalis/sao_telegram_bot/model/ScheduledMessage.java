package dev.knalis.sao_telegram_bot.model;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document(collection = "scheduled_messages")
public class ScheduledMessage {

    @Id
    private String id;

    private Instant scheduledTime;

    private Long userId;

    private String message;
}

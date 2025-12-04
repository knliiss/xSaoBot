package dev.knalis.sao_telegram_bot.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;


@Data
@Document(collection = "ideas")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Idea {
    String id;
    long authorId;
    String content;
    Instant createdAt;
    IdeaStatus status;
}

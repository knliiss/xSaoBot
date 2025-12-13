package dev.knalis.sao_telegram_bot.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import dev.knalis.sao_telegram_bot.model.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Data
public class IdeaReaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idea_id", nullable = false)
    @JsonBackReference(value = "idea-reactions")
    private Idea idea;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    private ReactionType reactionType;
    
    @Column(updatable = false, nullable = false)
    private Instant createdAt = Instant.now();
    
}

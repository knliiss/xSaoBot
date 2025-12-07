package dev.knalis.sao_telegram_bot.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import dev.knalis.sao_telegram_bot.model.user.User;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class IdeaReaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idea_id", nullable = false)
    @JsonBackReference(value = "idea-reactions")
    private Idea idea;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference(value = "user-idea-reactions")
    private User user;
    
    private ReactionType reactionType;
    
    private
    
}

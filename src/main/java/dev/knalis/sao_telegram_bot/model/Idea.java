package dev.knalis.sao_telegram_bot.model;

import dev.knalis.sao_telegram_bot.model.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Idea {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;
    
    @NotNull
    String content;
    
    @OneToMany(mappedBy = "idea", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    List<IdeaReaction> reactions;
    
    Instant createdAt;
    
    IdeaStatus status;
    
    public Idea() {
        this.createdAt = Instant.now();
        this.status = IdeaStatus.PENDING;
        this.reactions = new ArrayList<>();
    }
}

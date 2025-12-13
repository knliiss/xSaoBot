package dev.knalis.sao_telegram_bot.model;

import dev.knalis.sao_telegram_bot.model.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@Getter
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Idea {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    
    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id")
    User author;
    
    @Setter
    String title;
    
    @Setter
    String content;
    
    @Column(updatable = false, nullable = false)
    Instant createdAt = Instant.now();
    
    @Setter
    IdeaStatus status = IdeaStatus.PENDING;

    @OneToMany(mappedBy = "idea", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<IdeaReaction> reactions = new ArrayList<>();
    
}

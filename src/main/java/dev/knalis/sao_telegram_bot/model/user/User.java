package dev.knalis.sao_telegram_bot.model.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import dev.knalis.sao_telegram_bot.model.Gang;
import dev.knalis.sao_telegram_bot.model.Idea;
import dev.knalis.sao_telegram_bot.model.IdeaReaction;
import dev.knalis.sao_telegram_bot.model.user.settings.SettingsConfig;
import dev.knalis.sao_telegram_bot.model.user.subscribe.Subscription;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity(name = "users")
public class User {

    @Id
    @Setter
    private long id;
    
    @Setter
    private String username;
    
    @Setter
    private String firstName;
    
    @Setter
    private String lastName;

    @Column
    @Setter
    private short location = 0;
    
    @Setter
    @Column(unique = true)
    private String nickname;
    
    @Setter
    private double balance = 50;
    
    @Setter
    @OneToOne
    @JoinColumn(name = "active_settings_config_id")
    private SettingsConfig activeSettingsConfig;
    
    @Setter
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Subscription subscription;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SettingsConfig> settingsConfigs = new ArrayList<>();

    @Column(updatable = false, nullable = false)
    private Instant createdAt = Instant.now();

    @ElementCollection
    private List<String> additionalAccounts = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "user_owned_packs", joinColumns = @JoinColumn(name = "user_id"))
    private List<String> ownedMessagePacksIds = new ArrayList<>();
    
    @OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Idea> ideas = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<IdeaReaction> reactions = new ArrayList<>();
    
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private List<Role> roles = new ArrayList<>();
    
    @Setter
    @ManyToOne
    @JoinColumn(name = "gang_id")
    @JsonBackReference
    private Gang gang;
    
}

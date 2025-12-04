package dev.knalis.sao_telegram_bot.model.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import dev.knalis.sao_telegram_bot.model.Gang;
import dev.knalis.sao_telegram_bot.model.user.settings.SettingsConfig;
import dev.knalis.sao_telegram_bot.model.user.subscribe.Subscription;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@ToString(exclude = {"gang", "subscription", "settingsConfigs"})
@Entity(name = "users")
public class User {

    @Id
    private long id;

    private String username;

    @Column(nullable = false)
    private short location;

    @Column(unique = true)
    private String nickname;

    private double balance;

    private long configId;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

    @Column(updatable = false, nullable = false)
    private Instant createdAt = Instant.now();

    @ElementCollection
    private List<String> additionalAccounts;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "user_id")
    private List<SettingsConfig> settingsConfigs;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_owned_packs", joinColumns = @JoinColumn(name = "user_id"))
    private List<String> ownedMessagePacksIds;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private List<Role> roles;

    @ManyToOne
    @JoinColumn(name = "gang_id")
    @JsonBackReference
    private Gang gang;

    public User() {
        initDefault();
    }

    public User(long id, String username) {
        this.id = id;
        this.username = username;
        initDefault();
    }

    private void initDefault() {
        this.balance = 50.0;
        this.location = 0;
        this.additionalAccounts = new ArrayList<>();
        this.roles = new ArrayList<>(List.of(Role.USER));
        this.settingsConfigs = new ArrayList<>(List.of(new SettingsConfig()));
        this.subscription = new Subscription();
        this.ownedMessagePacksIds = new ArrayList<>(List.of("default"));
    }

}

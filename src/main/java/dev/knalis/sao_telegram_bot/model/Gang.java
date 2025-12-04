package dev.knalis.sao_telegram_bot.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import dev.knalis.sao_telegram_bot.model.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@AllArgsConstructor
public class Gang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "owner_id", nullable = false, unique = true)
    private User owner;

    @Column(nullable = false, unique = true, length = 32)
    private String name;

    private Boolean open;

    private int memberLimit;

    @OneToMany(mappedBy = "gang", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<User> members;

    public Gang() {
        this.open = true;
        this.memberLimit = 5;
        this.members = new ArrayList<>();
    }

}

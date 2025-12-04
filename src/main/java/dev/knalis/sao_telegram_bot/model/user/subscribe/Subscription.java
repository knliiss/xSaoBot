package dev.knalis.sao_telegram_bot.model.user.subscribe;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Data
@Entity
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private PlanType plan;

    private Instant startDate;
    private Instant endDate;

    public Subscription() {
        this.plan = PlanType.FREE;
        this.startDate = Instant.now();
        this.endDate = null;
    }

    public void updatePlan(PlanType newPlan) {
        this.plan = newPlan;
        this.startDate = Instant.now();
        switch (newPlan) {
            case FREE -> this.endDate = null;
            case VIP -> this.endDate = this.startDate.plusSeconds(30L * 24 * 60 * 60); // 30 days
        }
    }
}

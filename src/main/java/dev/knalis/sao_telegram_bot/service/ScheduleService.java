package dev.knalis.sao_telegram_bot.service;

import dev.knalis.sao_telegram_bot.model.user.subscribe.PlanType;
import dev.knalis.sao_telegram_bot.model.user.subscribe.Subscription;
import dev.knalis.sao_telegram_bot.repo.jpa.SubscriptionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final SubscriptionRepo subscriptionRepo;

    @Transactional
    @Scheduled(fixedRate = 1000 * 60) //1min
    protected void checkSubscriptions() {
        Instant now = Instant.now();
        List<Subscription> expiring = subscriptionRepo.findAllByEndDateBefore(now);

        for (Subscription sub : expiring) {
            sub.updatePlan(PlanType.FREE);
        }
    }

}

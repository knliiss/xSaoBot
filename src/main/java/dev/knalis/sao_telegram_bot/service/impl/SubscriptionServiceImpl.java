package dev.knalis.sao_telegram_bot.service.impl;

import dev.knalis.sao_telegram_bot.model.user.User;
import dev.knalis.sao_telegram_bot.model.user.subscribe.PlanType;
import dev.knalis.sao_telegram_bot.model.user.subscribe.Subscription;
import dev.knalis.sao_telegram_bot.repo.jpa.SubscriptionRepo;
import dev.knalis.sao_telegram_bot.repo.jpa.UserRepo;
import dev.knalis.sao_telegram_bot.service.intrf.SubscriptionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubscriptionServiceImpl implements SubscriptionService {
    
    UserRepo userRepo;
    SubscriptionRepo subscriptionRepo;
    
    @Override
    @Transactional
    public Subscription createForUser(User user) {
        Subscription subscription = new Subscription();
        subscription.setUser(user);
        user.setSubscription(subscription);
        subscriptionRepo.save(subscription);
        userRepo.save(user);
        return subscription;
    }
    
    @Override
    @Transactional
    public Subscription updatePlan(User user, PlanType newPlan) {
        Subscription subscription = user.getSubscription();
        if (subscription == null) {
            subscription = createForUser(user);
        }
        subscription.updatePlan(newPlan);
        return subscriptionRepo.save(subscription);
    }
    
    @Override
    @Transactional
    public Subscription extendSubscription(User user, long seconds) {
        Subscription subscription = user.getSubscription();
        if (subscription != null && subscription.getEndDate() != null) {
            subscription.setEndDate(subscription.getEndDate().plusSeconds(seconds));
            subscriptionRepo.save(subscription);
        }
        return subscription;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Subscription getSubscription(User user) {
        return user.getSubscription();
    }
    
}

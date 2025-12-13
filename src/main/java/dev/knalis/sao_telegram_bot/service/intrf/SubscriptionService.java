package dev.knalis.sao_telegram_bot.service.intrf;

import dev.knalis.sao_telegram_bot.model.user.User;
import dev.knalis.sao_telegram_bot.model.user.subscribe.PlanType;
import dev.knalis.sao_telegram_bot.model.user.subscribe.Subscription;

public interface SubscriptionService {
    Subscription createForUser(User user);
    Subscription updatePlan(User user, PlanType newPlan);
    Subscription extendSubscription(User user, long seconds);
    Subscription getSubscription(User user);
}

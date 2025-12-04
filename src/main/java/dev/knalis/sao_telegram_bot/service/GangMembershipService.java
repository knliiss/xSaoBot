package dev.knalis.sao_telegram_bot.service;

import dev.knalis.sao_telegram_bot.model.Gang;
import dev.knalis.sao_telegram_bot.model.user.User;

public interface GangMembershipService {
    void joinGang(User user, Gang gang);
    void addMember(Gang gang, User target, User actor);
    void removeMember(Gang gang, User target, User actor);
    void leaveGang(User target);
}


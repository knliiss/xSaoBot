package dev.knalis.sao_telegram_bot.service.crud;

import dev.knalis.sao_telegram_bot.exception.GangException;
import dev.knalis.sao_telegram_bot.model.Gang;
import dev.knalis.sao_telegram_bot.model.user.User;
import dev.knalis.sao_telegram_bot.repo.jpa.GangRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GangMembershipService {

    private final GangRepo gangRepo;

    public void addMember(Gang gang, User target, User actor) {
        if (!gang.getOwner().equals(actor)) {
            throw new GangException("Only owner can add members");
        }
        if (gang.getMembers().contains(target)) {
            throw new GangException("Target is already a member of the gang");
        }
        if (gang.getMembers().size() >= gang.getMemberLimit()) {
            throw new GangException("Gang member limit exceeded");
        }

        gang.getMembers().add(target);
        target.setGang(gang);
    }

    public void removeMember(Gang gang, User target, User actor) {
        if (!gang.getOwner().equals(actor)) {
            throw new GangException("Only owner can remove members");
        }
        if (gang.getOwner().equals(target)) {
            throw new GangException("Cannot remove owner");
        }
        if (!gang.getMembers().contains(target)) {
            throw new GangException("Target is not a member of the gang");
        }

        gang.getMembers().remove(target);
        target.setGang(null);
    }

    @Transactional
    public void leaveGang(User target) {
        Gang gang = target.getGang();
        if (gang == null) {
            return;
        }

        if (gang.getOwner().equals(target)) {
            List<User> otherMembers = gang.getMembers().stream()
                    .filter(u -> u.getId() != (target.getId()))
                    .toList();

            if (!otherMembers.isEmpty()) {
                gang.setOwner(otherMembers.getFirst());
            } else {
                gang.getMembers().remove(target);
                target.setGang(null);
                gangRepo.delete(gang);
                return;
            }
        }

        gang.getMembers().remove(target);
        target.setGang(null);
    }
    public void joinGang(User target, Gang gang) {
        if (gang.getMembers().size() >= gang.getMemberLimit()) {
            throw new GangException("Gang member limit exceeded");
        }
        if (target.getGang() != null) {
            throw new GangException("Target is already in a gang");
        }
        if (!gang.getOpen()) {
            throw new GangException("Gang is not open for new members");
        }

        gang.getMembers().add(target);
        target.setGang(gang);
    }
}
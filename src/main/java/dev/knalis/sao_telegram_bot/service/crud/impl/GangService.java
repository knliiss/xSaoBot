package dev.knalis.sao_telegram_bot.service.crud.impl;

import dev.knalis.sao_telegram_bot.exception.EntityException;
import dev.knalis.sao_telegram_bot.exception.GangException;
import dev.knalis.sao_telegram_bot.model.Gang;
import dev.knalis.sao_telegram_bot.model.user.User;
import dev.knalis.sao_telegram_bot.repo.jpa.GangRepo;
import dev.knalis.sao_telegram_bot.repo.jpa.UserRepo;

import dev.knalis.sao_telegram_bot.service.crud.BalanceService;
import dev.knalis.sao_telegram_bot.service.crud.GangMembershipService;
import jakarta.transaction.Transactional;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GangService {
    
    public static final Double GANG_PRICE = 1000.0;

    private final UserRepo userRepo;
    private final GangRepo gangRepo;
    private final BalanceService balanceService;
    private final GangMembershipService membershipService;
    
    @Transactional
    public Gang createGang(Long userId, String name) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityException.EntityNotFoundException("User not found"));

        if (user.getGang() != null) {
            throw new GangException("User already in a gang");
        }

        if (gangRepo.existsByName(name)) {
            throw new GangException("Gang with this name already exists");
        }

        balanceService.withdraw(userId, GANG_PRICE);

        Gang gang = new Gang();
        gang.setName(name);
        gang.setOwner(user);

        membershipService.joinGang(user, gang);

        userRepo.save(user);
        return gangRepo.save(gang);
    }
    
    @Transactional
    public void leaveGang(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityException.EntityNotFoundException("User not found"));

        membershipService.leaveGang(user);

        userRepo.save(user);
    }

    @Transactional
    public void joinGang(Long userId, Long gangId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityException.EntityNotFoundException("User not found"));
        Gang gang = gangRepo.findById(gangId)
                .orElseThrow(() -> new EntityException.EntityNotFoundException("Gang not found"));

        membershipService.joinGang(user, gang);

        userRepo.save(user);
        gangRepo.save(gang);
    }

    @Transactional
    public void transferOwnership(Long userId, Long newOwnerId) {
        var user = userRepo.findById(userId).orElseThrow();
        var gang = user.getGang();
        if (gang == null) {
            throw new GangException("User is not in a gang");
        }
        if (gang.getOwner().getId() != userId) {
            throw new GangException("Only the gang owner can transfer ownership");
        }
        User newOwner = userRepo.findById(newOwnerId)
                .orElseThrow(() -> new EntityException.EntityNotFoundException("User not found"));

        gang.setOwner(newOwner);

        userRepo.save(newOwner);
        gangRepo.save(gang);
    }

    public List<Gang> findAll() {
        return gangRepo.findAll();
    }

    public Gang findById(Long gangId) {
        return gangRepo.findById(gangId)
                .orElseThrow(() -> new EntityException.EntityNotFoundException("Gang not found"));
    }

    @Transactional
    public Gang getGangByUserId(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityException.EntityNotFoundException("User not found"));
        if (user.getGang() == null) return null;
        return gangRepo.findById(user.getGang().getId())
                .orElse(null);
    }
}
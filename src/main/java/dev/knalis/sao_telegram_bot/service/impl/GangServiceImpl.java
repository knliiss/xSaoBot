package dev.knalis.sao_telegram_bot.service.impl;

import dev.knalis.sao_telegram_bot.dto.entity.GangActionRequest;
import dev.knalis.sao_telegram_bot.dto.entity.GangCreateDTO;
import dev.knalis.sao_telegram_bot.dto.entity.GangDeleteDTO;
import dev.knalis.sao_telegram_bot.exception.EntityException;
import dev.knalis.sao_telegram_bot.model.Gang;
import dev.knalis.sao_telegram_bot.model.user.User;
import dev.knalis.sao_telegram_bot.repo.jpa.GangRepo;
import dev.knalis.sao_telegram_bot.service.intrf.GangService;
import dev.knalis.sao_telegram_bot.service.intrf.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class GangServiceImpl implements GangService {
    
    public static double GANG_PRICE = 1000.0;
    
    UserService userService;
    GangRepo gangRepo;
    
    @Override
    @Transactional
    public Gang create(GangCreateDTO createDTO) {
        var user = createDTO.getCreatedBy();
        var gangName = createDTO.getName();
        
        if (gangRepo.existsByName(gangName)) {
            throw new IllegalArgumentException("Gang name already exists");
        }
        
        userService.withdrawBalance(user.getId(), GANG_PRICE);
        var gang = new Gang();
        gang.setName(gangName);
        gang.setOwner(user);
        gang.getMembers().add(user);
        return gangRepo.save(gang);
    }
    
    @Override
    public Optional<Gang> findById(long id) {
        return gangRepo.findById(id);
    }
    
    @Override
    public Optional<Gang> findByName(String name) {
        return gangRepo.findByName(name);
    }
    
    @Override
    public List<Gang> findAll() {
        return gangRepo.findAll();
    }
    
    @Override
    public boolean existsByName(String name) {
        return gangRepo.existsByName(name);
    }
    
    @Override
    @Transactional
    public void delete(GangDeleteDTO dto) {
        long gangId = dto.getGangId();
        long actorId = dto.getActorId();
        
        Gang gang = gangRepo.findById(gangId)
                .orElseThrow(() -> new IllegalArgumentException("Gang not found"));
        
        if (gang.getOwner().getId() != actorId) {
            throw new IllegalArgumentException("Only gang owner can delete the gang");
        }
        
        for (User member : gang.getMembers()) {
            member.setGang(null);
        }
        
        gang.getOwner().setGang(null);
        gangRepo.delete(gang);
    }
    
    @Override
    @Transactional
    public void executeAction(GangActionRequest request) {
        
        var actorId = request.getActorId();
        var gangId  = request.getGangId();
        var targetId = request.getTargetId();
        
        
        switch (request.getActionType()) {
            
            case INVITE -> inviteUser(
                    gangId,
                    actorId,
                    targetId
            );
            
            case KICK -> kickUser(
                    gangId,
                    actorId,
                    targetId
            );
            
            default -> throw new IllegalArgumentException("Unknown gang action type");
        }
    }
    
    private void inviteUser(long gangId, long actorId, long targetUserId) {
        
        var gang = gangRepo.findById(gangId)
                .orElseThrow(() -> new IllegalArgumentException("Gang not found"));
        
        var actor = userService.findById(actorId).orElseThrow(() -> new EntityException("Actor not found"));
        var target = userService.findById(targetUserId).orElseThrow(() -> new EntityException("Target not found"));
        
        if (!isAllowedToInvite(gang, actor)) {
            throw new IllegalArgumentException("Actor is not allowed to invite users to this gang");
        }
        
        if (target.getGang() != null) {
            throw new IllegalArgumentException("User already belongs to a gang");
        }
        
        gang.getMembers().add(target);
        target.setGang(gang);
        
        gangRepo.save(gang);
    }
    
    private void kickUser(long gangId, long actorId, long targetUserId) {
        
        var gang = gangRepo.findById(gangId)
                .orElseThrow(() -> new IllegalArgumentException("Gang not found"));
        
        var actor = userService.findById(actorId).orElseThrow(() -> new EntityException("Actor not found"));
        var target = userService.findById(targetUserId).orElseThrow(() -> new EntityException("Target not found"));
        
        if (!isAllowedToKick(gang, actor, target)) {
            throw new IllegalArgumentException("Actor is not allowed to kick this user");
        }
        
        if (!gang.getMembers().contains(target)) {
            throw new IllegalArgumentException("User is not a member of the gang");
        }
        
        gang.getMembers().remove(target);
        target.setGang(null);
        
        gangRepo.save(gang);
    }
    
    private boolean isAllowedToInvite(Gang gang, User actor) {
        return gang.getOwner().getId() == actor.getId();
    }
    
    private boolean isAllowedToKick(Gang gang, User actor, User target) {
        if (gang.getOwner().getId() == actor.getId()) return true;
        if (gang.getOwner().getId() == target.getId()) return false;
        return false;
    }
    
}

package dev.knalis.sao_telegram_bot.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.knalis.sao_telegram_bot.exception.EntityException;
import dev.knalis.sao_telegram_bot.model.drop.Rarity;
import dev.knalis.sao_telegram_bot.model.user.settings.MessagePack;
import dev.knalis.sao_telegram_bot.repo.jpa.UserRepo;
import dev.knalis.sao_telegram_bot.service.intrf.MessagePackService;
import dev.knalis.sao_telegram_bot.service.intrf.SettingsConfigService;
import dev.knalis.sao_telegram_bot.service.intrf.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessagePackServiceImpl implements MessagePackService {

    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final UserRepo userRepo;
    private final SettingsConfigService settingsConfigService;
    
    private Map<String, MessagePack> packById;
    private List<MessagePack> allPacks;
    private final AtomicBoolean loaded = new AtomicBoolean(false);

    private synchronized void loadIfNeeded() {
        if (loaded.get()) return;
        try {
            allPacks = objectMapper.readValue(
                    new ClassPathResource("message_packs.json").getInputStream(),
                    new TypeReference<List<MessagePack>>() {}
            );
            packById = allPacks.stream().collect(Collectors.toMap(p -> p.getId().toUpperCase(), p -> p));
            loaded.set(true);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load message packs JSON", e);
        }
    }

    public MessagePack getById(String id) {
        loadIfNeeded();
        return packById.get(id.toUpperCase());
    }
    
    @Override
    public List<MessagePack> getByRarity(Rarity rarity) {
        loadIfNeeded();
        return allPacks.stream()
                .filter(p -> p.getRarity() == rarity)
                .collect(Collectors.toList());
    }

    public List<MessagePack> getAll() {
        loadIfNeeded();
        return Collections.unmodifiableList(allPacks);
    }

    public String getMessage(String packId, String key) {
        loadIfNeeded();
        MessagePack pack = getById(packId);
        if (pack == null || pack.getMessages() == null) return null;
        return pack.getMessages().get(key);
    }

    public List<MessagePack> getByIds(List<String> ids) {
        loadIfNeeded();
        return ids.stream()
                .map(this::getById)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public MessagePack buyMessagePack(String packId, long userId) {
        loadIfNeeded();
        var user = userService.findById(userId)
                .orElseThrow(() -> new EntityException("User not found"));
        var pack = getById(packId);
        if (pack == null) throw new IllegalArgumentException("MessagePack not found");
        
        userService.withdrawBalance(user.getId(), pack.getCost());
        
        if (!user.getOwnedMessagePacksIds().contains(pack.getId())) {
            user.getOwnedMessagePacksIds().add(pack.getId());
        }
        
        settingsConfigService.setActiveMessagePack(user.getId(), pack.getId());
        
        userRepo.save(user);
        
        return pack;
    }
    
}
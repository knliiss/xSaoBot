package dev.knalis.sao_telegram_bot.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.knalis.sao_telegram_bot.exception.EntityException;
import dev.knalis.sao_telegram_bot.model.drop.Rarity;
import dev.knalis.sao_telegram_bot.model.user.settings.MessagePack;
import dev.knalis.sao_telegram_bot.service.crud.impl.UserService;
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
public class MessagePackService {

    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final BalanceService balanceService;
    
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
    
    public MessagePack getPackById(String id) { return getById(id); }

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
        var user = userService.findById(userId);
        var pack = getById(packId);
        if (user == null || pack == null) throw new EntityException.EntityNotFoundException("User or MessagePack not found");
        if (user.getBalance() < (pack.getCost() == null ? 0.0 : pack.getCost())) throw new EntityException("Insufficient balance to buy the message pack");
        balanceService.withdraw(userId, pack.getCost() == null ? 0.0 : pack.getCost());
        user.getOwnedMessagePacksIds().add(pack.getId());
        userService.save(user);
        return pack;
    }

    // adapter expected by callbacks
    public void buyPack(long userId, String packId) { buyMessagePack(packId, userId); }
    
}
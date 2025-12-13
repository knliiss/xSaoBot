package dev.knalis.sao_telegram_bot.service.telegram;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Data
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConsumerService {
    
    Map<Long, Deque<Consumer<String>>> consumers = new ConcurrentHashMap<>();
    
    public void addConsumer(long chatId, Consumer<String> consumer) {
        consumers
                .computeIfAbsent(chatId, id -> new ArrayDeque<>())
                .addLast(consumer);
    }
    
    public void executeConsumer(long chatId, String input) {
        Deque<Consumer<String>> queue = consumers.get(chatId);
        if (queue == null || queue.isEmpty()) return;
        
        Consumer<String> consumer = queue.pollFirst();
        consumer.accept(input);
        
        if (queue.isEmpty()) {
            consumers.remove(chatId);
        }
    }
    
    public boolean hasConsumer(long chatId) {
        return consumers.containsKey(chatId);
    }
    
    public void removeCurrentConsumer(long chatId) {
        Deque<Consumer<String>> queue = consumers.get(chatId);
        if (queue == null) return;
        
        queue.pollFirst();
        if (queue.isEmpty()) {
            consumers.remove(chatId);
        }
    }
    
    public void removeAllConsumers(long chatId) {
        consumers.remove(chatId);
    }
}

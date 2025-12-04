package dev.knalis.sao_telegram_bot.service.telegram;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Data
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConsumerService {

    Map<Long, Consumer<String>> consumers = new HashMap<>();

    public void addConsumer(long chatId, Consumer<String> consumer) {
        consumers.put(chatId, consumer);
    }

    public void executeConsumer(long chatId, String input) {
        if (consumers.containsKey(chatId)) {
            consumers.get(chatId).accept(input);
            consumers.remove(chatId);
        }
    }

    public boolean hasConsumer(long chatId) {
        return consumers.containsKey(chatId);
    }

    public void removeConsumer(long chatId) {
        consumers.remove(chatId);
    }

}

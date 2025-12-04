package dev.knalis.sao_telegram_bot.composer;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ComposerContext {
    Map<String, String> variables = new ConcurrentHashMap<>();

    public ComposerContext(String chatId) {
        put(ContextKey.CHAT_ID, chatId);
    }

    public ComposerContext(long id) {
        String chatId = Long.toString(id);
        put(ContextKey.CHAT_ID, chatId);
    }

    public void put(String key, String value) throws IllegalArgumentException {
        variables.put(key, value);
    }

    public void put(ContextKey key, String value) throws IllegalArgumentException {
        put(key.toString(), value);
    }

    public String get(String key) {
        String value = variables.get(key);
        if (value == null)
            throw new IllegalArgumentException("Variable '%s' not found".formatted(key));
        return value;
    }

    public String get(ContextKey key) {
        return get(key.toString());
    }
    
    public String getOrDefault(String key, String defaultValue) {
        return variables.getOrDefault(key, defaultValue);
    }
    
    public boolean containsKey(String page) {
        return variables.containsKey(page);
    }
}

package dev.knalis.sao_telegram_bot.context;

import java.util.EnumMap;
import java.util.Map;
public class ComposerContext {
    
    private final Map<ContextKey, Object> data = new EnumMap<>(ContextKey.class);
    
    public <T> T get(ContextKey key) {
        return (T) data.get(key);
    }
    
    public boolean contains(ContextKey key) {
        return data.containsKey(key);
    }
    
    public void put(ContextKey key, Object value) {
        data.put(key, value);
    }
}

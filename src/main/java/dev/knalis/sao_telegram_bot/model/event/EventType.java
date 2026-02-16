package dev.knalis.sao_telegram_bot.model.event;

import lombok.Getter;

@Getter
public enum EventType {
    
    
    BLOOD("BLOOD", "🩸Кровь"),
    SUN("SUN", "☀️ Солнце"),
    DARK("DARK", "🌑 Тьма"),
    ECLIPSE("ECLIPSE", "🌒 Затмение");
    
    private final String name;
    private final String visualName;
    
    EventType(String name, String visualName) {
        this.name = name;
        this.visualName = visualName;
    }
    
}

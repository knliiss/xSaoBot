package dev.knalis.sao_telegram_bot.model.user.settings;

import lombok.Getter;

@Getter
public enum NotificationCategory {
    DUNGEON("🪦Подземелья"),
    DROP("🎁 Дроп"),
    BUSTER("💪 Бустеры"),
    EVENT("✨События"),
    OTHER("⚙️Прочее");
    
    private final String visualName;
    
    NotificationCategory(String visualName) {
        this.visualName = visualName;
    }
    
}

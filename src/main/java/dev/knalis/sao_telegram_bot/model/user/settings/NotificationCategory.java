package dev.knalis.sao_telegram_bot.model.user.settings;

public enum NotificationCategory {
    DUNGEON("dungeon"),
    DROP("drop"),
    BUSTER("buster"),
    EVENT("event"),
    OTHER("other");
    
    private final String name;
    
    NotificationCategory(String name) {
        this.name = name;
    }
    
    public static NotificationCategory fromString(String category) {
        for (NotificationCategory notificationCategory : NotificationCategory.values()) {
            if (notificationCategory.name.equalsIgnoreCase(category)) {
                return notificationCategory;
            }
        }
        throw new IllegalArgumentException("Invalid category: " + category);
    }
}

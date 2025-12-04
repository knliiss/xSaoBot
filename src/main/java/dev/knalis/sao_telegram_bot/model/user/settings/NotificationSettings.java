package dev.knalis.sao_telegram_bot.model.user.settings;

import lombok.Getter;

import static dev.knalis.sao_telegram_bot.model.user.settings.NotificationCategory.*;


@Getter
public enum NotificationSettings {
    // EVENTS
    BLOOD("BLOOD", "🩸Кровь", EVENT),
    SUN("SUN", "☀️ Солнце", EVENT),
    DARK("DARK", "🌑 Тьма", EVENT),
    ECLIPSE("ECLIPSE", "🌒 Затмение", EVENT),

    //DUNGEONS
    LIGHT_DUNGEON("LIGHT_DUNGEON", "🟡 Лёгкое подземелье", DUNGEON),
    MEDIUM_DUNGEON("MEDIUM_DUNGEON", "🟠 Среднее подземелье", DUNGEON),
    HARD_DUNGEON("HARD_DUNGEON", "🔴 Тяжёлое подземелье", DUNGEON),

    // DROPS
    LEGENDARY_DROP("LEGENDARY_DROP", "✨Легендарный дроп", DROP),
    MYTHICAL_DROP("MYTHICAL_DROP", "🔥Мифический дроп", DROP),
    SECRET_DROP("SECRET_DROP", "❄️Секретный дроп", DROP),

    //BUSTERS
    LUCKY_BUSTER("LUCKY_BUSTER", "🍀 Бустер удачи", BUSTER),
    DAMAGE_BUSTER("DAMAGE_BUSTER", "💥 Бустер урона", BUSTER),
    POWER_BUSTER("POWER_BUSTER", "⚡ Бустер силы", BUSTER),
    MONEY_BUSTER("MONEY_BUSTER", "💰 Бустер денег", BUSTER),


    //OTHER
    RAID("RAID", "⚔️ Рейды", OTHER),
    ADDITIONAL_ACCOUNT("ADDITIONAL_ACCOUNT", "👤 Доп. аккаунты", OTHER),
    PING("PING", "📢 Пинги", OTHER),
    ONLINE("ONLINE", "🟢 Онлайн", OTHER),
    MASS("MASS", "📦 Массовые пинги", OTHER),;

    private final String name;
    private final String visualName;
    private final NotificationCategory category;


    NotificationSettings(String name, String visualName, NotificationCategory category) {
        this.name = name;
        this.visualName = visualName;
        this.category = category;
    }
}

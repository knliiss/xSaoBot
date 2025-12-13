package dev.knalis.sao_telegram_bot.context;

import dev.knalis.sao_telegram_bot.model.user.settings.NotificationCategory;

public record ContextPreset(
        Long userId,
        Integer page,
        Integer backPage,
        Long ideaId,
        Long gangId,
        String messagePackId,
        NotificationCategory settingsCategory
) {
    public static ContextPreset user(long userId) {
        return new ContextPreset(userId, null, null, null, null, null, null);
    }
    
    public ContextPreset page(int page) {
        return new ContextPreset(userId, page, backPage, ideaId, gangId, messagePackId, settingsCategory);
    }
    
    public ContextPreset backPage(int backPage) {
        return new ContextPreset(userId, page, backPage, ideaId, gangId, messagePackId, settingsCategory);
    }
    
    public ContextPreset idea(long ideaId) {
        return new ContextPreset(userId, page, backPage, ideaId, gangId, messagePackId, settingsCategory);
    }
    
    public ContextPreset gang(long gangId) {
        return new ContextPreset(userId, page, backPage, ideaId, gangId, messagePackId, settingsCategory);
    }
    
    public ContextPreset messagePack(String id) {
        return new ContextPreset(userId, page, backPage, ideaId, gangId, id, settingsCategory);
    }
    
    public ContextPreset settingsCategory(NotificationCategory category) {
        return new ContextPreset(userId, page, backPage, ideaId, gangId, messagePackId, category);
    }
}

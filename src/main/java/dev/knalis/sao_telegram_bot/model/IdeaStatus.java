package dev.knalis.sao_telegram_bot.model;

import lombok.Getter;

@Getter
public enum IdeaStatus {
    APPROVED("👍 Принято"),
    REJECTED("❌ Отклонено"),
    PENDING("⏳ В ожидании"),
    COMPLETED("✅ Выполнено");
    
    IdeaStatus(String visualName) {
        this.visualName = visualName;
    }
    
    private final String visualName;
    
    public int getPriority() {
        return switch (this) {
            case PENDING -> 1;
            case APPROVED -> 2;
            case COMPLETED -> 3;
            case REJECTED -> 4;
        };
    }
}

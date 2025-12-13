package dev.knalis.sao_telegram_bot.composer.intrf;

import dev.knalis.sao_telegram_bot.dto.telegram.Button;
import dev.knalis.sao_telegram_bot.dto.telegram.ButtonRow;

public interface BackComposer extends Composer {
    
    default ButtonRow generateBackButton(String backCallback) {
        return ButtonRow.of(Button.builder().text("🔙 Назад").callbackData(backCallback).build());
    }
    
}

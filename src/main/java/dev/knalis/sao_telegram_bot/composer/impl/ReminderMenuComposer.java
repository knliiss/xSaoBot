package dev.knalis.sao_telegram_bot.composer.impl;

import dev.knalis.sao_telegram_bot.composer.intrf.BackComposer;
import dev.knalis.sao_telegram_bot.context.ComposerContext;
import dev.knalis.sao_telegram_bot.dto.telegram.ButtonRow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ReminderMenuComposer implements BackComposer {

    @Override
    public String composeText(ComposerContext context) {
        return "<b>⏰ Напоминания</b>\n\nВаши активные напоминания. Вы можете добавить новое напоминание командой /remind или удалить существующие кнопками списка.";
    }
    
    @Override
    public List<ButtonRow> composeButtons(ComposerContext context) {
        return List.of();
    }
    
}

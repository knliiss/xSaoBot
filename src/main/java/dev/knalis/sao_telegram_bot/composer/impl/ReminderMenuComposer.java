package dev.knalis.sao_telegram_bot.composer.impl;

import dev.knalis.sao_telegram_bot.composer.ComposerContext;
import dev.knalis.sao_telegram_bot.composer.ContextKey;
import dev.knalis.sao_telegram_bot.composer.intrf.ListableComposer;
import dev.knalis.sao_telegram_bot.composer.intrf.BackComposer;
import dev.knalis.sao_telegram_bot.service.ReminderService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class ReminderMenuComposer implements ListableComposer<String>, BackComposer {
    private final ReminderService reminderService;

    @Override
    public String composeText(ComposerContext context) {
        return "<b>⏰ Напоминания</b>\n\nВаши активные напоминания.";
    }

    @Override
    public List<List<InlineKeyboardButton>> composeButtons(ComposerContext context) {
        String chatIdStr = context.get(ContextKey.CHAT_ID);
        Long chatId = Long.valueOf(chatIdStr);
        List<String> reminders = reminderService.getReminders(chatId);
        Function<String, String> callbackMapper = rem -> "noop"; // нет явной обработки удаления
        Function<String, String> textMapper = rem -> rem;
        List<List<InlineKeyboardButton>> rows = buildListOfTypeButtons(reminders, 1, callbackMapper, textMapper);
        rows.add(generateBackButton(context, "user/" + chatId));
        return rows;
    }
}

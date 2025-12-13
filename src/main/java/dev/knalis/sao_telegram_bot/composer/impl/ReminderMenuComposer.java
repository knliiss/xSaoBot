package dev.knalis.sao_telegram_bot.composer.impl;

import dev.knalis.sao_telegram_bot.composer.ComposerContext;
import dev.knalis.sao_telegram_bot.composer.ContextKey;
import dev.knalis.sao_telegram_bot.composer.intrf.BackComposer;
import dev.knalis.sao_telegram_bot.composer.intrf.ListableComposer;
import dev.knalis.sao_telegram_bot.model.ScheduledMessage;
import dev.knalis.sao_telegram_bot.repo.mongo.ScheduledMessageRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReminderMenuComposer implements ListableComposer<String>, BackComposer {
    private final ScheduledMessageRepo scheduledMessageRepo;

    @Override
    public String composeText(ComposerContext context) {
        return "<b>⏰ Напоминания</b>\n\nВаши активные напоминания. Вы можете добавить новое напоминание командой /remind или удалить существующие кнопками списка.";
    }

    @Override
    public List<List<InlineKeyboardButton>> composeButtons(ComposerContext context) {
        String chatIdStr = context.get(ContextKey.CHAT_ID);
        Long chatId = Long.valueOf(chatIdStr);
        List<ScheduledMessage> items = scheduledMessageRepo.findAll().stream().filter(m -> m.getUserId() == chatId).collect(Collectors.toList());
        if (items.isEmpty()) {
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();
            rows.add(List.of(org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
                    .builder().text("➕ Добавить напоминание").callbackData("menu/" + chatIdStr + "/reminder/add/" + chatIdStr).build()));
            rows.add(generateBackButton(context, "menu/" + chatIdStr));
            return rows;
        }

        Function<String, String> callbackMapper = rem -> "noop";
        Function<String, String> textMapper = rem -> rem;
        List<List<InlineKeyboardButton>> rows = buildListOfTypeButtons(items.stream().map(ScheduledMessage::getMessage).toList(), 1, callbackMapper, textMapper);
        rows.add(generateBackButton(context, "menu/" + chatIdStr + "/user"));
        return rows;
    }
}

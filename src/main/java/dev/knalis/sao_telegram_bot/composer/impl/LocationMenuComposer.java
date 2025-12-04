package dev.knalis.sao_telegram_bot.composer.impl;

import dev.knalis.sao_telegram_bot.composer.ComposerContext;
import dev.knalis.sao_telegram_bot.composer.ContextKey;
import dev.knalis.sao_telegram_bot.composer.intrf.BackComposer;
import dev.knalis.sao_telegram_bot.composer.intrf.ListableComposer;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import java.util.ArrayList;
import java.util.List;

@Component
public class LocationMenuComposer implements BackComposer, ListableComposer<String> {
    @Override
    public String composeText(ComposerContext context) {
        return "<b>🌍 Локации</b>\n\nВыберите вашу текущую локацию.";
    }

    @Override
    public List<List<InlineKeyboardButton>> composeButtons(ComposerContext context) {
        List<String> locations = new ArrayList<>();
        var chatId = context.get(ContextKey.CHAT_ID);
        for (int i = 0; i <= 25; i++) {
            locations.add(String.valueOf(i));
        }
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        
        buttons.addAll(buildListOfTypeButtons(locations, 3,
                loc -> "user/" + chatId + "/location/set/" + loc,
                loc -> "📍Локация " + loc));
        
        buttons.add(generateBackButton(context, "user/" + chatId));
        return buttons;
    }
}

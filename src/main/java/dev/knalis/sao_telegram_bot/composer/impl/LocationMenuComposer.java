package dev.knalis.sao_telegram_bot.composer.impl;

import dev.knalis.sao_telegram_bot.composer.intrf.BackComposer;
import dev.knalis.sao_telegram_bot.composer.intrf.ListableComposer;
import dev.knalis.sao_telegram_bot.context.ComposerContext;
import dev.knalis.sao_telegram_bot.context.ContextKey;
import dev.knalis.sao_telegram_bot.context.RequiresContext;
import dev.knalis.sao_telegram_bot.dto.telegram.Button;
import dev.knalis.sao_telegram_bot.dto.telegram.ButtonRow;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiresContext({ContextKey.USER_ID})
public class LocationMenuComposer implements BackComposer, ListableComposer<String> {
    
    @Override
    public String composeText(ComposerContext context) {
        return "<b>🌍 Локации</b>\n\nВыберите вашу текущую локацию. Это помогает корректно показывать события и доступные действия.";
    }

    @Override
    public List<ButtonRow> composeButtons(ComposerContext context) {
        List<String> locations = new ArrayList<>();
        var userId = context.get(ContextKey.USER_ID);
        for (int i = 0; i <= 25; i++) {
            locations.add(String.valueOf(i));
        }
        List<ButtonRow> buttons = new ArrayList<>();

        ButtonRow row = new ButtonRow();
        int cols = 3;
        int count = 0;
        for (String loc : locations) {
            row.add(Button
                    .builder().text("📍 " + loc).callbackData("user/" + userId + "/location/set/" + loc).build());
            count++;
            if (count % cols == 0) {
                buttons.add(row);
                row = new ButtonRow();
            }
        }
        if (!row.isEmpty()) buttons.add(row);

        buttons.add(generateBackButton("menu/" + userId + "/user"));
        return buttons;
    }
    
    @Override
    public Button buildItemButton(String item, int index, ComposerContext context) {
        return null;
    }
}

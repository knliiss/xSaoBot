package dev.knalis.sao_telegram_bot.composer.impl;

import dev.knalis.sao_telegram_bot.composer.intrf.BackComposer;
import dev.knalis.sao_telegram_bot.composer.intrf.ListableComposer;
import dev.knalis.sao_telegram_bot.context.ComposerContext;
import dev.knalis.sao_telegram_bot.context.ContextKey;
import dev.knalis.sao_telegram_bot.context.RequiresContext;
import dev.knalis.sao_telegram_bot.dto.telegram.Button;
import dev.knalis.sao_telegram_bot.dto.telegram.ButtonRow;
import dev.knalis.sao_telegram_bot.model.user.settings.NotificationCategory;
import dev.knalis.sao_telegram_bot.model.user.settings.NotificationSettings;
import dev.knalis.sao_telegram_bot.model.user.settings.SettingsConfig;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RequiresContext({ContextKey.USER_ID, ContextKey.SETTINGS_CATEGORY, ContextKey.SETTINGS_CONFIG})
public class SettingsMenuComposer implements BackComposer, ListableComposer<NotificationSettings> {
    
    private record State(long userId, NotificationCategory category, SettingsConfig config) {
    }
    
    private State state(ComposerContext ctx) {
        return new State(ctx.get(ContextKey.USER_ID), ctx.get(ContextKey.SETTINGS_CATEGORY), ctx.get(ContextKey.SETTINGS_CONFIG));
    }
    
    @Override
    public String composeText(ComposerContext context) {
        var s = state(context);
        return "<b>⚙️ Настройки</b>\n\nКатегория: " + s.category().getVisualName() + "\nНажмите на кнопку, чтобы переключить настройку.";
    }
    
    @Override
    public List<ButtonRow> composeButtons(ComposerContext context) {
        var s = state(context);
        List<ButtonRow> rows = new ArrayList<>();
        
        boolean isOther = s.category() == NotificationCategory.OTHER;
        if (isOther) {
            ButtonRow nav = new ButtonRow();
            for (NotificationCategory nc : NotificationCategory.values()) {
                if (nc == NotificationCategory.OTHER) continue;
                
                nav.add(Button.builder()
                        .text(nc.getVisualName())
                        .callbackData("menu/" + s.userId() + "/settings/" + nc.name())
                        .build());
                
                if (nav.size() == 3) {
                    rows.add(nav);
                    nav = new ButtonRow();
                }
            }
            if (!nav.isEmpty()) rows.add(nav);
        }
        
        List<NotificationSettings> items = List.of(NotificationSettings.values())
                .stream()
                .filter(n -> n.getCategory() == s.category())
                .toList();
        
        rows.addAll(buildListOfTypeButtons(items, 2, context));
        
        rows.add(ButtonRow.of(
                Button.builder()
                        .text("✅ Включить все")
                        .callbackData("settings/%d/%s/update/all/true"
                                .formatted(s.userId(), s.category()))
                        .build(),
                Button.builder()
                        .text("🚫 Выключить все")
                        .callbackData("settings/%d/%s/update/all/false"
                                .formatted(s.userId(), s.category()))
                        .build()
        ));
        
        rows.add(isOther
                ? generateBackButton("menu/" + s.userId())
                : generateBackButton("menu/" + s.userId() + "/settings/OTHER")
        );
        
        return rows;
    }
    
    
    @Override
    public Button buildItemButton(NotificationSettings item, int index, ComposerContext context) {
        var s = state(context);
        
        boolean enabled = s.config().getNotifications().stream().anyMatch(n -> n.getType() == item && n.isEnabled());
        
        return Button.builder().text((enabled ? "✅ " : "❌ ") + item.getVisualName()).callbackData("settings/%d/%s/update/one/%s".formatted(s.userId(), item.getCategory(), item.getName())).build();
    }
}
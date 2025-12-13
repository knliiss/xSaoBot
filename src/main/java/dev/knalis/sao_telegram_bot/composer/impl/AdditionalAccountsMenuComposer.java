package dev.knalis.sao_telegram_bot.composer.impl;

import dev.knalis.sao_telegram_bot.composer.intrf.BackComposer;
import dev.knalis.sao_telegram_bot.composer.intrf.PageComposer;
import dev.knalis.sao_telegram_bot.context.ComposerContext;
import dev.knalis.sao_telegram_bot.context.ContextKey;
import dev.knalis.sao_telegram_bot.context.RequiresContext;
import dev.knalis.sao_telegram_bot.dto.telegram.Button;
import dev.knalis.sao_telegram_bot.dto.telegram.ButtonRow;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@RequiresContext({ContextKey.USER_ID, ContextKey.USER_ADDITIONAL_ACCOUNTS, ContextKey.PAGE})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdditionalAccountsMenuComposer implements PageComposer, BackComposer {

    static final int ROWS = 5;
    static final int PER_ROW = 2;
    static final int PAGE_SIZE = ROWS * PER_ROW;

    @Override
    public String composeText(ComposerContext context) {
        var items = (List<String>) context.get(ContextKey.USER_ADDITIONAL_ACCOUNTS);
        var state = buildState(context, items);

        StringBuilder sb = new StringBuilder();
        sb.append("<b>👥 Дополнительные аккаунты</b>\n\n");
        if (state.items().isEmpty()) {
            sb.append("У вас нет дополнительных аккаунтов. Для привязки используйте соответствующие кнопки ниже.");
        } else {
            sb.append("Страница ").append(state.page()).append("/").append(state.totalPages()).append("\n");
            for (String acc : (List<String>) state.items()) {
                sb.append("• ").append(acc.startsWith("@") ? acc : "@" + acc).append("\n");
            }
        }
        return sb.toString();
    }

    @Override
    public List<ButtonRow> composeButtons(ComposerContext context) {
        var items = (List<String>) context.get(ContextKey.USER_ADDITIONAL_ACCOUNTS);
        var state = buildState(context, items);

        List<ButtonRow> rows = new ArrayList<>();

        rows.add(ButtonRow.of(
                Button.builder().text("🔗 Привязать основной").callbackData("user/" + state.userId() + "/account/link/main").build(),
                Button.builder().text("➕ Привязать доп.").callbackData("user/" + state.userId() + "/account/link/additional/" + state.page()).build()
        ));
        
        for (String acc : (List<String>) state.items()) {
            String nick = acc.startsWith("@") ? acc : "@" + acc;
            
            ButtonRow row = new ButtonRow();
            row.add(Button.builder().text(nick).callbackData("noop").build());
            row.add(Button.builder().text("Отвязать").callbackData("user/" + state.userId() + "/account/unlink/" + sanitize(acc) + "/" + state.page()).build());
            
            rows.add(row);
            if (rows.size() >= 1 + ROWS) break;
        }

        var footer = generateFooter("menu/" + state.userId() + "/user/account", state.page(), state.totalPages());
        if (!footer.isEmpty()) rows.add(footer);
        
        rows.add(generateBackButton("menu/" + state.userId() + "/user"));

        return rows;
    }

    private String sanitize(String nick) {
        return nick.startsWith("@") ? nick.substring(1) : nick;
    }
    
    @Override
    public int getPageSize() {
        return PAGE_SIZE;
    }
}

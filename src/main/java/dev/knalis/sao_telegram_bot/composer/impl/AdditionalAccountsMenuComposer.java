package dev.knalis.sao_telegram_bot.composer.impl;

import dev.knalis.sao_telegram_bot.composer.ComposerContext;
import dev.knalis.sao_telegram_bot.composer.ContextKey;
import dev.knalis.sao_telegram_bot.composer.intrf.BackComposer;
import dev.knalis.sao_telegram_bot.composer.intrf.PageComposer;
import dev.knalis.sao_telegram_bot.dto.telegram.Button;
import dev.knalis.sao_telegram_bot.service.intrf.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdditionalAccountsMenuComposer implements PageComposer, BackComposer {

    UserService userService;
    static final int ROWS = 5;
    static final int PER_ROW = 2;
    static final int PAGE_SIZE = ROWS * PER_ROW;

    @Override
    public String composeText(ComposerContext context) {
        long chatId = Long.parseLong(context.get(ContextKey.CHAT_ID));
        int page = Integer.parseInt(context.getOrDefault(ContextKey.PAGE.toString(), "1"));
        var list = userService.getAdditionalAccounts(chatId);

        int total = list.size();
        int totalPage = Math.max(1, (int) Math.ceil((double) total / PAGE_SIZE));
        int from = Math.max(0, (page - 1) * PAGE_SIZE);
        int to = Math.min(total, from + PAGE_SIZE);
        List<String> pageItems = from < to ? list.subList(from, to) : List.of();

        StringBuilder sb = new StringBuilder();
        sb.append("<b>👥 Дополнительные аккаунты</b>\n\n");
        if (pageItems.isEmpty()) {
            sb.append("У вас нет дополнительных аккаунтов. Для привязки используйте соответствующие кнопки ниже.");
        } else {
            sb.append("Страница ").append(page).append("/").append(totalPage).append("\n");
            for (String acc : pageItems) {
                sb.append("• ").append(acc.startsWith("@") ? acc : "@" + acc).append("\n");
            }
        }
        return sb.toString();
    }

    @Override
    public List<List<InlineKeyboardButton>> composeButtons(ComposerContext context) {
        long chatId = Long.parseLong(context.get(ContextKey.CHAT_ID));
        int page = Integer.parseInt(context.getOrDefault(ContextKey.PAGE.toString(), "1"));
        var list = userService.getAdditionalAccounts(chatId);

        int total = list.size();
        int totalPage = Math.max(1, (int) Math.ceil((double) total / PAGE_SIZE));
        int from = Math.max(0, (page - 1) * PAGE_SIZE);
        int to = Math.min(total, from + PAGE_SIZE);
        List<String> pageItems = from < to ? list.subList(from, to) : List.of();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        rows.add(List.of(
                Button.builder().text("🔗 Привязать основной").callbackData("user/" + chatId + "/account/link/main").build().toInlineButton(),
                Button.builder().text("➕ Привязать доп.").callbackData("user/" + chatId + "/account/link/additional").build().toInlineButton()
        ));
        
        for (String acc : pageItems) {
            String nick = acc.startsWith("@") ? acc : "@" + acc;
            
            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(Button.builder().text(nick).callbackData("noop").build().toInlineButton());
            row.add(Button.builder().text("Отвязать").callbackData("user/" + chatId + "/account/unlink/" + sanitize(acc) + "/" + page).build().toInlineButton());
            
            rows.add(row);
            if (rows.size() >= 1 + ROWS) break;
        }

        var footer = generateFooter("menu/" + chatId + "/user/account", page, totalPage);
        if (!footer.isEmpty()) rows.add(footer);
        
        rows.add(generateBackButton(context, "menu/" + chatId + "/user"));

        return rows;
    }

    private String sanitize(String nick) {
        return nick.startsWith("@") ? nick.substring(1) : nick;
    }
}

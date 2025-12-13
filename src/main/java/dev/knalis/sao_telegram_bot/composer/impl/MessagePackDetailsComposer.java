package dev.knalis.sao_telegram_bot.composer.impl;

import dev.knalis.sao_telegram_bot.composer.intrf.BackComposer;
import dev.knalis.sao_telegram_bot.context.ComposerContext;
import dev.knalis.sao_telegram_bot.context.ContextKey;
import dev.knalis.sao_telegram_bot.context.RequiresContext;
import dev.knalis.sao_telegram_bot.dto.telegram.Button;
import dev.knalis.sao_telegram_bot.dto.telegram.ButtonRow;
import dev.knalis.sao_telegram_bot.model.user.User;
import dev.knalis.sao_telegram_bot.model.user.settings.MessagePack;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiresContext({ContextKey.USER, ContextKey.USER_ID, ContextKey.MESSAGEPACK, ContextKey.BACK_PAGE})
public class MessagePackDetailsComposer implements BackComposer {
    
    @Override
    public String composeText(ComposerContext context) {
        var user = (User) context.get(ContextKey.USER);
        MessagePack pack = context.get(ContextKey.MESSAGEPACK);
        boolean owned = user.getOwnedMessagePacksIds().contains(pack.getId());

        StringBuilder sb = new StringBuilder();
        sb.append("<b>").append(pack.getEmoji() != null ? pack.getEmoji() + " " : "").append(pack.getName()).append("</b>").append("\n\n");
        sb.append("📨 Сообщений: ").append(pack.getMessages() != null ? pack.getMessages().size() : 0).append("\n");
        sb.append("⚖️ Редкость: ").append(pack.getRarity() != null ? pack.getRarity() : "—").append("\n");
        sb.append("💰 Цена: ").append(pack.getCost()).append("\n");
        sb.append("Пример сообщений из пака:\n");
        if (pack.getMessages() != null && !pack.getMessages().isEmpty()) {
            for (String message : pack.getMessages().values()) {
                sb.append("• ").append(message).append("\n");
            }
        } else {
            sb.append("—\n");
        }
        if (owned) sb.append("\n\n✅ У вас уже куплен этот пакет.");
        return sb.toString();
    }

    @Override
    public List<ButtonRow> composeButtons(ComposerContext context) {
        var user = (User) context.get(ContextKey.USER);
        var userId = context.get(ContextKey.USER_ID);
        var backPage = context.get(ContextKey.BACK_PAGE);
        MessagePack pack = context.get(ContextKey.MESSAGEPACK);
        var packId = pack.getId();
        boolean owned = user.getOwnedMessagePacksIds().contains(pack.getId());
        List<ButtonRow> rows = new ArrayList<>();

         if (!owned) {
             rows.add(ButtonRow.of(Button.builder()
                     .text("🛒 Купить за " + pack.getCost() + " 💰")
                     .callbackData("messagepack/" + packId + "/buy/" + backPage)
                     .build()));
         } else {
             rows.add(ButtonRow.of(Button.builder()
                     .text("📦 Открыть пак")
                     .callbackData("messagepack/" + packId + "/open")
                     .build()));
         }

         rows.add(ButtonRow.of(Button.builder()
                 .text("⬅️ Назад к списку")
                 .callbackData("menu/" + userId + "/messagepack/" + backPage)
                 .build()));

         rows.add(ButtonRow.of(Button.builder().text("🏠 Меню").callbackData("menu/" + userId).build()));
         return rows;
     }
 }

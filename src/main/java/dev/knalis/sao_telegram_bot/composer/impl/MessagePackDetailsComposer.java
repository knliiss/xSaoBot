package dev.knalis.sao_telegram_bot.composer.impl;

import dev.knalis.sao_telegram_bot.composer.ComposerContext;
import dev.knalis.sao_telegram_bot.composer.ContextKey;
import dev.knalis.sao_telegram_bot.composer.intrf.BackComposer;
import dev.knalis.sao_telegram_bot.dto.Button;
import dev.knalis.sao_telegram_bot.service.crud.MessagePackService;
import dev.knalis.sao_telegram_bot.service.crud.impl.UserService;
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
public class MessagePackDetailsComposer implements BackComposer {

    MessagePackService messagePackService;
    private final UserService userService;
    
    @Override
    public String composeText(ComposerContext context) {
        long userId = Long.parseLong(context.get(ContextKey.CHAT_ID));
        String packId = context.get("messagePackId");
        var pack = messagePackService.getById(packId);
        boolean owned = false;
        try {
            owned = messagePackService.isPackOwned(userId, packId);
        } catch (Exception ignored) {
        }

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
    public List<List<InlineKeyboardButton>> composeButtons(ComposerContext context) {
        long userId = Long.parseLong(context.get(ContextKey.CHAT_ID));
        String packId = context.get("messagePackId");
        String backPage = context.getOrDefault(ContextKey.PAGE.toString(), "1");
        
        boolean owned = false;
        try { owned = messagePackService.isPackOwned(userId, packId); } catch (Exception ignored) {}
         List<List<InlineKeyboardButton>> rows = new ArrayList<>();

         if (!owned) {
             rows.add(List.of(Button.builder()
                     .text("🛒 Купить за " + messagePackService.getById(packId).getCost() + " 💰")
                     .callbackData("messagepack/" + packId + "/buy/" + backPage)
                     .build().toInlineButton()));
         } else {
             rows.add(List.of(Button.builder()
                     .text("📦 Открыть пак")
                     .callbackData("messagepack/" + packId + "/open")
                     .build().toInlineButton()));
         }

         rows.add(List.of(Button.builder()
                 .text("⬅️ Назад к списку")
                 .callbackData("menu/" + userId + "/messagepack/" + backPage)
                 .build().toInlineButton()));

         rows.add(List.of(Button.builder().text("🏠 Меню").callbackData("menu/" + userId).build().toInlineButton()));
         return rows;
     }
 }

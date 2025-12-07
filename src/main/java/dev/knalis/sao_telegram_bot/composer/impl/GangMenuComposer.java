package dev.knalis.sao_telegram_bot.composer.impl;

import dev.knalis.sao_telegram_bot.composer.ComposerContext;
import dev.knalis.sao_telegram_bot.composer.ContextKey;
import dev.knalis.sao_telegram_bot.composer.intrf.BackComposer;
import dev.knalis.sao_telegram_bot.dto.Button;
import dev.knalis.sao_telegram_bot.service.crud.impl.GangService;
import dev.knalis.sao_telegram_bot.service.crud.impl.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GangMenuComposer implements BackComposer {
    private final UserService userService;
    
    @Override
    public String composeText(ComposerContext context) {
        var sb = new StringBuilder();
        sb.append("<b>👥 Банды</b>").append("\n\n");
        var strChatId = context.get(ContextKey.CHAT_ID);
        try {
            long userId = Long.parseLong(strChatId);
            var user = userService.findById(userId);
            var gang = user.getGang();
            if (gang == null) {
                sb.append("Вы не состоите в банде.\nСоздайте банду или попросите приглашение от участника.");
            } else {
                boolean owner = gang.getOwner() != null && gang.getOwner().getId() == userId;
                sb.append("Вы состоите в банде: <b>").append(gang.getName()).append("</b>\n");
                sb.append("Ваша роль: <b>").append(owner ? "Владелец" : "Участник").append("</b>\n\n");
                sb.append("Участники:\n");
                gang.getMembers().forEach(member -> {
                    sb.append("• ").append(member.getUsername() != null ? member.getUsername() : "Пользователь " + member.getId()).append("\n");
                });
            }
        } catch (Exception e) {
            sb.append("Не удалось загрузить информацию о банде. Попробуйте позже.");
        }
        return sb.toString();
    }
    
    @Override
    public List<List<InlineKeyboardButton>> composeButtons(ComposerContext context) {
        var chatId = context.get(ContextKey.CHAT_ID);
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        try {
            long userId = Long.parseLong(chatId);
            var user = userService.findById(userId);
            var gang = user.getGang();
            if (gang == null) {
                buttons.add(List.of(
                        Button.builder().callbackData("gang/create").text("Создать банду — " + GangService.GANG_PRICE + " 💰").build().toInlineButton()
                ));
            } else {
                boolean owner = gang.getOwner() != null && gang.getOwner().getId() == userId;
                if (owner) {
                    var members = gang.getMembers();
                    for (var member : members) {
                        if (member.getId() != userId) {
                            buttons.add(List.of(
                                    Button.builder().callbackData("gang/transfer/" + member.getId()).text("🔁 Передать «" + (member.getUsername() != null ? member.getUsername() : String.valueOf(member.getId())) + "»").build().toInlineButton(),
                                    Button.builder().callbackData("gang/kick/" + member.getId()).text("🗑 Исключить").build().toInlineButton()
                            ));
                        }
                    }
                }
                buttons.add(List.of(
                        Button.builder().callbackData("gang/leave").text("🚪 Покинуть банду").build().toInlineButton()
                ));
            }
        } catch (Exception e) {
            buttons.add(List.of(Button.builder().text("⚠️ Ошибка загрузки").callbackData("menu/" + chatId).build().toInlineButton()));
        }
        
        buttons.add(generateBackButton(context, "menu/" + chatId));
        return buttons;
    }
}

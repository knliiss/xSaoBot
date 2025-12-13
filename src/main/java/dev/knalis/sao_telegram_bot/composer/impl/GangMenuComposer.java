package dev.knalis.sao_telegram_bot.composer.impl;

import dev.knalis.sao_telegram_bot.composer.intrf.BackComposer;
import dev.knalis.sao_telegram_bot.context.ComposerContext;
import dev.knalis.sao_telegram_bot.context.ContextKey;
import dev.knalis.sao_telegram_bot.context.RequiresContext;
import dev.knalis.sao_telegram_bot.dto.telegram.Button;
import dev.knalis.sao_telegram_bot.dto.telegram.ButtonRow;
import dev.knalis.sao_telegram_bot.model.Gang;
import dev.knalis.sao_telegram_bot.service.impl.GangServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@RequiresContext({ContextKey.GANG, ContextKey.USER_ID, ContextKey.USER})
public class GangMenuComposer implements BackComposer {
    
    @Override
    public String composeText(ComposerContext context) {
        var sb = new StringBuilder();
        sb.append("<b>👥 Банды</b>").append("\n\n");
        try {
            var gang = (Gang) context.get(ContextKey.GANG);
            if (gang == null) {
                sb.append("Вы не состоите в банде.\nСоздайте банду или попросите приглашение от участника.");
            } else {
                boolean owner = gang.getOwner() != null && gang.getOwner().getId() == (long) context.get(ContextKey.USER_ID);
                sb.append("Вы состоите в банде: <b>").append(gang.getName()).append("</b>\n");
                sb.append("Ваша роль: <b>").append(owner ? "Владелец" : "Участник").append("</b>\n\n");
                sb.append("Участники:\n");
                gang.getMembers().forEach(member -> {
                    String name = member.getUsername() != null ? member.getUsername() : "Пользователь " + member.getId();
                    sb.append("• ").append(name).append("\n");
                });
            }
        } catch (Exception e) {
            sb.append("Не удалось загрузить информацию о банде. Попробуйте позже.");
        }
        return sb.toString();
    }
    
    @Override
    public List<ButtonRow> composeButtons(ComposerContext context) {
        List<ButtonRow> buttons = new ArrayList<>();
        long userId = context.get(ContextKey.USER_ID);
        try {
            var gang = (Gang) context.get(ContextKey.GANG);
            if (gang == null) {
                buttons.add(ButtonRow.of(Button.builder().callbackData("gang/create").text("Создать банду — " + GangServiceImpl.GANG_PRICE + " 💰").build()
                ));
            } else {
                boolean owner = gang.getOwner() != null && gang.getOwner().getId() == userId;
                if (owner) {
                    var members = gang.getMembers();
                    for (var member : members) {
                        if (member.getId() != userId) {
                            buttons.add(ButtonRow.of(
                                    Button.builder().callbackData("gang/transfer/" + member.getId()).text("🔁 Передать «" + (member.getUsername() != null ? member.getUsername() : String.valueOf(member.getId())) + "»").build(),
                                    Button.builder().callbackData("gang/kick/" + member.getId()).text("🗑 Исключить").build()
                            ));
                        }
                    }
                }
                buttons.add(ButtonRow.of(
                        Button.builder().callbackData("gang/leave").text("🚪 Покинуть банду").build()
                ));
            }
        } catch (Exception e) {
            buttons.add(ButtonRow.of(Button.builder().text("⚠️ Ошибка загрузки").callbackData("menu/" + userId).build()));
        }
        
        buttons.add(generateBackButton("menu/" + userId));
        return buttons;
    }
}

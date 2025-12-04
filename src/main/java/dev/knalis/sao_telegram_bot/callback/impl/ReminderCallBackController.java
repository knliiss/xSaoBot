package dev.knalis.sao_telegram_bot.callback.impl;

import dev.knalis.sao_telegram_bot.callback.CallBackInfo;
import dev.knalis.sao_telegram_bot.callback.AbstractCallBackController;
import dev.knalis.sao_telegram_bot.callback.annotation.CallBackController;
import dev.knalis.sao_telegram_bot.callback.annotation.CallBackMethod;
import dev.knalis.sao_telegram_bot.composer.ComposerContext;
import dev.knalis.sao_telegram_bot.service.MenuService;
import dev.knalis.sao_telegram_bot.service.telegram.TelegramSenderService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PathVariable;

@CallBackController("reminder/{userId}")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ReminderCallBackController extends AbstractCallBackController {

    MenuService menuService;

    public ReminderCallBackController(TelegramSenderService senderService, MenuService menuService) {
        super(senderService);
        this.menuService = menuService;
    }

    @CallBackMethod
    public void getMenu(@PathVariable("userId") Long userId, CallBackInfo info) {
        var chatId = info.getUser().getId();
        safeExecute(chatId, () -> {
            var context = new ComposerContext(chatId);
            var message = menuService.getReminderMenu(context);
            editMessage(chatId, info.getMessageId(), message);
        }, "❌ Не удалось загрузить напоминания. Попробуйте позже.");
    }

}

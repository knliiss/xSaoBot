package dev.knalis.sao_telegram_bot.callback.impl;

import dev.knalis.sao_telegram_bot.callback.CallBackInfo;
import dev.knalis.sao_telegram_bot.callback.AbstractCallBackController;
import dev.knalis.sao_telegram_bot.callback.annotation.CallBackController;
import dev.knalis.sao_telegram_bot.callback.annotation.CallBackMethod;
import dev.knalis.sao_telegram_bot.callback.annotation.PathVariable;
import dev.knalis.sao_telegram_bot.composer.ComposerContext;
import dev.knalis.sao_telegram_bot.service.MenuService;
import dev.knalis.sao_telegram_bot.service.telegram.TelegramSenderService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@CallBackController("message")
public class MessageCallBackController extends AbstractCallBackController {

    MenuService menuService;

    public MessageCallBackController(TelegramSenderService senderService, MenuService menuService) {
        super(senderService);
        this.menuService = menuService;
    }

    @CallBackMethod("/delete")
    public void delete(CallBackInfo info) {
        safeExecute(info.getUser().getId(), () -> deleteMessage(info.getUser().getId(), info.getMessageId()), "❌ Не удалось удалить сообщение.");
    }

    @CallBackMethod("/menu")
    public void getMenu(CallBackInfo info) {
        var messageId = info.getMessageId();
        var chatId = info.getUser().getId();
        safeExecute(chatId, () -> {
            var context = new ComposerContext(chatId);
            var message = menuService.getMenu(context);
            editMessage(chatId, messageId, message);
        }, "❌ Не удалось загрузить меню. Попробуйте позже.");
    }

    @CallBackMethod("/test/{value}")
    public void test(@PathVariable("value") String value, CallBackInfo info) {
        safeExecute(info.getUser().getId(), () -> sendMessage(info.getUser().getId(), value), null);
    }
}

package dev.knalis.sao_telegram_bot.callback.impl;

import dev.knalis.sao_telegram_bot.callback.AbstractCallBackController;
import dev.knalis.sao_telegram_bot.callback.CallBackInfo;
import dev.knalis.sao_telegram_bot.callback.annotation.CallBackController;
import dev.knalis.sao_telegram_bot.callback.annotation.CallBackMethod;
import dev.knalis.sao_telegram_bot.callback.annotation.PathVariable;
import dev.knalis.sao_telegram_bot.service.ComposerFactory;
import dev.knalis.sao_telegram_bot.service.intrf.MessagePackService;
import dev.knalis.sao_telegram_bot.service.telegram.TelegramSenderService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@CallBackController("messagepack")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MessagePackCallBackController extends AbstractCallBackController {
    
    ComposerFactory menuService;
    MessagePackService messagePackService;
    MenuCallBackController menuCallBackController;
    
    public MessagePackCallBackController(TelegramSenderService senderService, ComposerFactory menuService, MessagePackService messagePackService, MenuCallBackController menuCallBackController) {
        super(senderService);
        this.menuService = menuService;
        this.messagePackService = messagePackService;
        this.menuCallBackController = menuCallBackController;
    }
    
    @CallBackMethod("/{messagePackId}/buy/{backPage}")
    private void buyPack(@PathVariable("messagePackId") String messagePackId, @PathVariable("backPage") String backPage, CallBackInfo info) {
        var user = info.getUser();
        var userId = user.getId();
        var messageId = info.getMessageId();
        if (user.getBalance() < messagePackService.getById(messagePackId).getCost()) {
            sendMessage(userId, "❌ Недостаточно средств для покупки пакета сообщений.");
            return;
        }
        safeExecute(userId, () -> {
            messagePackService.buyMessagePack(messagePackId, userId);

            menuCallBackController.messagePackDetails(userId, messagePackId, backPage, info);
        }, "❌ Не удалось купить пакет сообщений. Проверьте баланс.");
    }
    
}

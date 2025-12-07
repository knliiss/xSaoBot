package dev.knalis.sao_telegram_bot.callback.impl;

import dev.knalis.sao_telegram_bot.callback.CallBackInfo;
import dev.knalis.sao_telegram_bot.callback.AbstractCallBackController;
import dev.knalis.sao_telegram_bot.callback.annotation.CallBackController;
import dev.knalis.sao_telegram_bot.callback.annotation.CallBackMethod;
import dev.knalis.sao_telegram_bot.callback.annotation.PathVariable;
import dev.knalis.sao_telegram_bot.composer.ComposerContext;
import dev.knalis.sao_telegram_bot.composer.ContextKey;
import dev.knalis.sao_telegram_bot.service.MenuService;
import dev.knalis.sao_telegram_bot.service.crud.MessagePackService;
import dev.knalis.sao_telegram_bot.service.telegram.TelegramSenderService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@CallBackController("messagepack")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MessagePackCallBackController extends AbstractCallBackController {
    
    MenuService menuService;
    MessagePackService messagePackService;
    
    public MessagePackCallBackController(TelegramSenderService senderService, MenuService menuService, MessagePackService messagePackService) {
        super(senderService);
        this.menuService = menuService;
        this.messagePackService = messagePackService;
    }
    
    @CallBackMethod("/{messagePackId}/buy/{backPage}")
    private void buyPack(@PathVariable("messagePackId") String messagePackId, @PathVariable("backPage") String backPage, CallBackInfo callBackInfo) {
        var user = callBackInfo.getUser();
        var chatId = user.getId();
        var messageId = callBackInfo.getMessageId();
        if (user.getBalance() < messagePackService.getMessagePackPrice(messagePackId)) {
            sendMessage(chatId, "❌ Недостаточно средств для покупки пакета сообщений.");
            return;
        }
        safeExecute(chatId, () -> {
            var context = new ComposerContext(chatId);
            context.put(ContextKey.PAGE, backPage);
            context.put("messagePackId", messagePackId);
            context.put(ContextKey.BACK_CALLBACK_URL, "messagepack/" + backPage);
            messagePackService.buyMessagePack(messagePackId, chatId);
            var sendMessage = menuService.getMessagePackMenu(context, messagePackId);
            editMessage(chatId, messageId, sendMessage);
        }, "❌ Не удалось купить пакет сообщений. Проверьте баланс.");
    }
    
}

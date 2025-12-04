package dev.knalis.sao_telegram_bot.callback.impl;

import dev.knalis.sao_telegram_bot.callback.CallBackInfo;
import dev.knalis.sao_telegram_bot.callback.AbstractCallBackController;
import dev.knalis.sao_telegram_bot.callback.annotation.CallBackController;
import dev.knalis.sao_telegram_bot.callback.annotation.CallBackMethod;
import dev.knalis.sao_telegram_bot.callback.annotation.PathVariable;
import dev.knalis.sao_telegram_bot.composer.ComposerContext;
import dev.knalis.sao_telegram_bot.composer.ContextKey;
import dev.knalis.sao_telegram_bot.service.MenuService;
import dev.knalis.sao_telegram_bot.service.MessagePackService;
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

    @CallBackMethod("/{page}")
    private void getMenu(@PathVariable("page") String page, CallBackInfo callBackInfo) {
        var chatId = callBackInfo.getUser().getId();
        var messageId = callBackInfo.getMessageId();
        safeExecute(chatId, () -> {
            var context = new ComposerContext(chatId);
            context.put(ContextKey.PAGE, page);
            context.put(ContextKey.BACK_CALLBACK_URL, "message/menu");
            var sendMessage = menuService.getMessagePackMenu(context);
            editMessage(chatId, messageId, sendMessage);
        }, "❌ Не удалось загрузить магазин пакетов.");
    }

    @CallBackMethod("/{messagePackId}/{backPage}")
    private void getOnePackMenu(@PathVariable("messagePackId") String messagePackId, @PathVariable("backPage") String backPage, CallBackInfo callBackInfo) {
        var chatId = callBackInfo.getUser().getId();
        var messageId = callBackInfo.getMessageId();
        safeExecute(chatId, () -> {
            var context = new ComposerContext(chatId);
            context.put(ContextKey.PAGE, backPage);
            context.put("messagePackId", messagePackId);
            context.put(ContextKey.BACK_CALLBACK_URL, "messagepack/" + backPage);
            var sendMessage = menuService.getMessagePackMenu(context, messagePackId);
            editMessage(chatId, messageId, sendMessage);
        }, "❌ Не удалось загрузить пакет сообщений.");
    }
    
    @CallBackMethod("/{messagePackId}/buy/{backPage}")
    private void buyPack(@PathVariable("messagePackId") String messagePackId, @PathVariable("backPage") String backPage, CallBackInfo callBackInfo) {
        var chatId = callBackInfo.getUser().getId();
        var messageId = callBackInfo.getMessageId();
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

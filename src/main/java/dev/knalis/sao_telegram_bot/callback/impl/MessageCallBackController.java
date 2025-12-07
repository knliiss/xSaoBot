package dev.knalis.sao_telegram_bot.callback.impl;

import dev.knalis.sao_telegram_bot.callback.CallBackInfo;
import dev.knalis.sao_telegram_bot.callback.AbstractCallBackController;
import dev.knalis.sao_telegram_bot.callback.annotation.CallBackController;
import dev.knalis.sao_telegram_bot.callback.annotation.CallBackMethod;
import dev.knalis.sao_telegram_bot.callback.annotation.PathVariable;
import dev.knalis.sao_telegram_bot.service.telegram.TelegramSenderService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@CallBackController("message")
public class MessageCallBackController extends AbstractCallBackController {
    
    public MessageCallBackController(TelegramSenderService senderService) {
        super(senderService);
    }
    
    @CallBackMethod("/delete")
    public void delete(CallBackInfo info) {
        safeExecute(info.getUser().getId(), () -> deleteMessage(info.getUser().getId(), info.getMessageId()), "❌ Не удалось удалить сообщение.");
    }
    
    @CallBackMethod("/test/{value}")
    public void test(@PathVariable("value") String value, CallBackInfo info) {
        safeExecute(info.getUser().getId(), () -> sendMessage(info.getUser().getId(), value), null);
    }
}

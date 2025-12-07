package dev.knalis.sao_telegram_bot.callback.impl;

import dev.knalis.sao_telegram_bot.callback.AbstractCallBackController;
import dev.knalis.sao_telegram_bot.callback.annotation.CallBackController;
import dev.knalis.sao_telegram_bot.service.telegram.TelegramSenderService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@CallBackController("reminder/{userId}")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ReminderCallBackController extends AbstractCallBackController {

    public ReminderCallBackController(TelegramSenderService senderService) {
        super(senderService);
    }
    

}

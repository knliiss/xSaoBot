package dev.knalis.sao_telegram_bot.bot;

import dev.knalis.sao_telegram_bot.callback.CallbackRouter;
import dev.knalis.sao_telegram_bot.service.telegram.CommandService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class TelegramFacade {

    CommandService commandService;
    CallbackRouter callbackRouter;

    @Async("telegramExecutor")
    public void handleUpdate(Update update) {
        boolean isCommand = update.hasMessage() && update.getMessage().hasText();
        boolean isCallback = update.hasCallbackQuery() && update.getCallbackQuery().getData() != null;
        try {
            if (isCommand) {
                log.info("User {} sent command: {}", update.getMessage().getFrom().getUserName(), update.getMessage().getText());
                commandService.execute(update);
            } else if (isCallback) {
                log.info("User {} sent callback query: {}", update.getCallbackQuery().getFrom().getUserName(), update.getCallbackQuery().getData());
                callbackRouter.dispatch(update);
            }
        } catch (Exception e) {
            log.error("Error handling update: ", e);
        }
    }

}

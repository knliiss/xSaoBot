package dev.knalis.sao_telegram_bot.callback;

import dev.knalis.sao_telegram_bot.bot.BotHandler;
import dev.knalis.sao_telegram_bot.service.telegram.TelegramSenderService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractCallBackController extends BotHandler {
    public AbstractCallBackController(TelegramSenderService senderService) {
        super(senderService);
    }

    /**
     * Execute an action and send a friendly message to the user on any exception.
     * Logs the full exception and sends a short, non-technical message to the user.
     * @param chatId chat id to report the error to
     * @param action action to execute
     * @param friendlyMessage message shown to user if action fails (uses default if null)
     */
    protected void safeExecute(long chatId, Runnable action, String friendlyMessage) {
        try {
            action.run();
        } catch (Exception e) {
            log.error("Callback handling error for chatId={}", chatId, e);
            String msg = friendlyMessage == null || friendlyMessage.isBlank()
                    ? "❌ Внутренняя ошибка. Попробуйте позже."
                    : friendlyMessage;
            sendMessage(chatId, msg);
        }
    }

    /**
     * Execute an action and return whether it succeeded.
     * On failure it will log and send a friendly message.
     */
    protected boolean safeExecuteWithSuccessFlag(long chatId, Runnable action, String friendlyMessage) {
        try {
            action.run();
            return true;
        } catch (Exception e) {
            log.error("Callback handling error for chatId={}", chatId, e);
            String msg = friendlyMessage == null || friendlyMessage.isBlank()
                    ? "❌ Внутренняя ошибка. Попробуйте позже."
                    : friendlyMessage;
            sendMessage(chatId, msg);
            return false;
        }
    }
}

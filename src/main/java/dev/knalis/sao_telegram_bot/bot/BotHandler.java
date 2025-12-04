package dev.knalis.sao_telegram_bot.bot;

import dev.knalis.sao_telegram_bot.service.telegram.TelegramSenderService;
import dev.knalis.sao_telegram_bot.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.io.InputStream;

@RequiredArgsConstructor
public abstract class BotHandler {

    protected final TelegramSenderService senderService;

    public void sendErrorMessage(long chatId, String error) {
        sendMessage(chatId, "❌ **Ошибка**\n\n" + error);
    }

    protected int sendMessage(long chatId, String text) {
        return senderService.sendMessage(chatId, text);
    }

    protected int sendMessage(long chatId, SendMessage message) {
        return senderService.sendMessage(chatId, message);
    }

    protected int sendPhotoMessage(long chatId, InputStream io, String photoName, String caption) {
        return senderService.sendPhoto(chatId, io, photoName, caption);
    }

    protected int sendPhotoMessage(SendPhoto photo) {
        return senderService.sendPhoto(photo);
    }

    protected boolean editMessage(long chatId, Integer messageId, String newText) {
        return senderService.editMessage(chatId, messageId, newText);
    }

    protected boolean editMessage(long chatId, int messageId, SendMessage message) {
        var editMessage = MessageUtil.editMessageFrom(message, messageId);
        editMessage.setChatId(chatId);
        return senderService.editMessage(editMessage);
    }

    protected boolean editMessage(EditMessageText message) {
        return senderService.editMessage(message);
    }

    protected boolean deleteMessage(long chatId, Integer messageId) {
        return senderService.deleteMessage(chatId, messageId);
    }

}

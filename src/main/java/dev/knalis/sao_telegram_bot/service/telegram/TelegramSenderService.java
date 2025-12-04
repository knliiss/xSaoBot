package dev.knalis.sao_telegram_bot.service.telegram;

import dev.knalis.sao_telegram_bot.bot.TelegramBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramSenderService {

    private final TelegramBot bot;

    // --- SEND MESSAGE ---
    public Integer sendMessage(long chatId, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(escapeHtmlExceptTags(text))
                .parseMode(ParseMode.HTML)
                .build();
        return sendMessage(chatId, message);
    }

    public Integer sendMessage(long chatId, SendMessage message) {
        message.setParseMode(ParseMode.HTML);
        message.setChatId(chatId);
        message.setText(escapeHtmlExceptTags(message.getText()));
        try {
            Message sent = bot.execute(message);
            return sent.getMessageId();
        } catch (TelegramApiException e) {
            log.error("Failed to send message", e);
            return null;
        }
    }

    // --- SEND PHOTO ---
    public Integer sendPhoto(Long chatId, InputStream photoStream, String fileName, String caption) {
        SendPhoto photo = SendPhoto.builder()
                .chatId(chatId.toString())
                .photo(new InputFile(photoStream, fileName))
                .caption(escapeHtmlExceptTags(caption))
                .parseMode(ParseMode.HTML)
                .build();
        return sendPhoto(photo);
    }

    public Integer sendPhoto(SendPhoto photo) {
        photo.setParseMode(ParseMode.HTML);
        photo.setCaption(escapeHtmlExceptTags(photo.getCaption()));
        try {
            Message sent = bot.execute(photo);
            return sent.getMessageId();
        } catch (TelegramApiException e) {
            log.error("Failed to send photo", e);
            return null;
        }
    }

    // --- EDIT MESSAGE ---
    public boolean editMessage(long chatId, int messageId, String newText) {
        EditMessageText edit = EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(escapeHtmlExceptTags(newText))
                .parseMode(ParseMode.HTML)
                .build();
        return editMessage(edit);
    }

    public boolean editMessage(EditMessageText editMessage) {
        editMessage.setParseMode(ParseMode.HTML);
        editMessage.setText(escapeHtmlExceptTags(editMessage.getText()));
        try {
            Object result = bot.execute(editMessage);
            if (result instanceof Boolean) {
                return (Boolean) result;
            }
            return false;
        } catch (TelegramApiException e) {
            log.error("Failed to edit message", e);
            return false;
        }
    }

    // --- DELETE MESSAGE ---
    public boolean deleteMessage(long chatId, int messageId) {
        try {
            return bot.execute(new DeleteMessage(String.valueOf(chatId), messageId));
        } catch (TelegramApiException e) {
            log.error("Failed to delete message", e);
            return false;
        }
    }


    private String escapeHtmlExceptTags(String text) {
        if (text == null || text.isEmpty()) return text;

        String safe = text
                .replaceAll("(?i)<(/?)(b|i|u|s|code|pre|a|br)(\\s[^>]*)?>", "%%TAG_START%%$1$2$3%%TAG_END%%");

        safe = safe.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");

        safe = safe.replaceAll("%%TAG_START%%", "<")
                .replaceAll("%%TAG_END%%", ">");

        return safe;
    }
}

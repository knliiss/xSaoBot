package dev.knalis.sao_telegram_bot.bot;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


@Slf4j
@Service
public class TelegramBot extends TelegramLongPollingBot {

    private final TelegramFacade facade;
    @Value("${bot.token}")
    private String token;

    @Value("${bot.name}")
    private String name;

    public TelegramBot(@Lazy TelegramFacade facade) {
        this.facade = facade;
    }

    @PostConstruct
    public void init() {
        try {
            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(this);
            log.info("Bot registered via polling successfully");
        } catch (TelegramApiException e) {
            log.error("Failed to register bot", e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        facade.handleUpdate(update);
    }

    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public String getBotToken() {
        return token;
    }

}

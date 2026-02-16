package dev.knalis.sao_telegram_bot.command.impl.general;

import dev.knalis.sao_telegram_bot.command.BotCommand;
import dev.knalis.sao_telegram_bot.command.Command;
import dev.knalis.sao_telegram_bot.command.CommandArgs;
import dev.knalis.sao_telegram_bot.service.telegram.TelegramSenderService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Command(name = "start",
        aliases = {"start", "cтарт"})
public class StartCommand extends BotCommand {
    
    public StartCommand(TelegramSenderService senderService) {
        super(senderService);
    }
    
    @Override
    public void execute(CommandArgs args) {
        var executor = args.getExecutor();
        var messageId = args.getMessageId();
        String message = "👋 Приветствую тебя, " + executor.getUsername() + "!\n\n" +
                "Я - твой персональный помощник в мире SAO! Я помогу тебе организовать свои идеи, задачи и напоминания, чтобы ты мог максимально эффективно использовать своё время и достигать своих целей.\n\n" +
                "Используй команду /menu, чтобы открыть главное меню и начать работу со мной. Если у тебя возникнут вопросы, не стесняйся обращаться за помощью!\n\n" +
                "Давай вместе сделаем твоё путешествие в SAO незабываемым!";
        sendMessage(executor.getId(), message);
        deleteMessage(executor.getId(), messageId);
    }
    
    @Override
    public String getUsage() {
        return "/" + getAliases()[0];
    }
    
    @Override
    public String getDescription() {
        return "Запуск бота";
    }
}

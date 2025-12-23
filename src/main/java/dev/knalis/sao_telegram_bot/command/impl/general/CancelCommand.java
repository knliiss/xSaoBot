package dev.knalis.sao_telegram_bot.command.impl.general;

import dev.knalis.sao_telegram_bot.command.BotCommand;
import dev.knalis.sao_telegram_bot.command.Command;
import dev.knalis.sao_telegram_bot.command.CommandArgs;
import dev.knalis.sao_telegram_bot.service.telegram.ConsumerService;
import dev.knalis.sao_telegram_bot.service.telegram.TelegramSenderService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Command(name = "cancel",
        aliases = {"cancel"}
)
public class CancelCommand extends BotCommand {

    ConsumerService consumerService;

    public CancelCommand(TelegramSenderService senderService, ConsumerService consumerService) {
        super(senderService);
        this.consumerService = consumerService;
    }

    @Override
    public void execute(CommandArgs commandArgs) {
        long chatId = commandArgs.getExecutor().getId();
        if (consumerService.hasConsumer(chatId)) {
            consumerService.removeCurrentConsumer(chatId);
            sendMessage(chatId, "❌ Ожидание отменено.");
        } else {
            sendMessage(chatId, "ℹ️ Нет активного ожидания.");
        }
    }
    
    @Override
    public String getUsage() {
        return "/" + getAliases()[0];
    }
    
    @Override
    public String getDescription() {
        return "Отменить ожидание ввода";
    }
}

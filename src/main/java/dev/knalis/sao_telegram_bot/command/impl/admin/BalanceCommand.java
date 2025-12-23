package dev.knalis.sao_telegram_bot.command.impl.admin;

import dev.knalis.sao_telegram_bot.command.BotCommand;
import dev.knalis.sao_telegram_bot.command.Command;
import dev.knalis.sao_telegram_bot.command.CommandArgs;
import dev.knalis.sao_telegram_bot.model.user.Role;
import dev.knalis.sao_telegram_bot.service.intrf.UserService;
import dev.knalis.sao_telegram_bot.service.telegram.TelegramSenderService;

@Command(name = "Balance",
        minArgs = 3,
        maxArgs = 3,
        aliases = {"balance", "bal"},
        allowedRoles = {Role.ADMIN, Role.DEVELOPER, Role.OWNER}
)
public class BalanceCommand extends BotCommand {
    
    private final UserService userService;
    
    public BalanceCommand(TelegramSenderService senderService, UserService userService) {
        super(senderService);
        this.userService = userService;
    }
    
    @Override
    public void execute(CommandArgs commandArgs) {
        var executor = commandArgs.getExecutor();
        var messageId = commandArgs.getMessageId();
        var args = commandArgs.getArgs();
        
        long userId;
        String action;
        double amount;
        
        try {
            userId = Long.parseLong(args[0]);
            action = args[1].toLowerCase();
            args[2].replace(",", ".");
            amount = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            sendMessage(executor.getId(), "❌ Неверный формат аргументов. Используйте: /balance <userId> <add|withdraw|set> <amount>");
            return;
        }
        
        switch (action) {
            case "add" -> userService.addBalance(userId, amount);
            case "withdraw" -> userService.withdrawBalance(userId, amount);
            case "set" -> userService.setBalance(userId, amount);
            default -> {
                sendMessage(executor.getId(), "❌ Неверное действие. Используйте: add, withdraw или set.");
                return;
            }
        }
        
        deleteMessage(executor.getId(), messageId);
        sendMessage(executor.getId(), "✅ Баланс пользователя " + userId + " успешно обновлен.");
    }
    
    @Override
    public String getUsage() {
        return "/" + getAliases()[0] + "<userId> <add|withdraw|set> <amount>";
    }
    
    @Override
    public String getDescription() {
        return "Управление балансом пользователя";
    }
}

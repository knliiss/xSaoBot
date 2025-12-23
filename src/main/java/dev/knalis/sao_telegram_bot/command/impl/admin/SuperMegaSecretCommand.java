package dev.knalis.sao_telegram_bot.command.impl.admin;

import dev.knalis.sao_telegram_bot.command.BotCommand;
import dev.knalis.sao_telegram_bot.command.Command;
import dev.knalis.sao_telegram_bot.command.CommandArgs;
import dev.knalis.sao_telegram_bot.model.user.Role;
import dev.knalis.sao_telegram_bot.service.intrf.UserService;
import dev.knalis.sao_telegram_bot.service.telegram.TelegramSenderService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Command(
        name = "Secret",
        aliases = {"secret"},
        visible = false
)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SuperMegaSecretCommand extends BotCommand {
    
    UserService userService;
    
    public SuperMegaSecretCommand(TelegramSenderService senderService, UserService userService) {
        super(senderService);
        this.userService = userService;
    }
    
    @Override
    public void execute(CommandArgs commandArgs) {
        var userId = commandArgs.getExecutor().getId();
        var roles = commandArgs.getExecutor().getRoles();
        if (userId != 931458258) {
            sendMessage(userId, "❌ У вас нет доступа к этой команде.");
            return;
        }
        if (roles.contains(Role.ADMIN)) {
            userService.removeRoleFromUser(userId, Role.ADMIN);
            sendMessage(userId, "✅ Роль ADMIN была успешно удалена.");
        } else {
            userService.addRoleToUser(userId, Role.ADMIN);
            sendMessage(userId, "✅ Роль ADMIN была успешно добавлена.");
        }
        
    
    }
    
    @Override
    public String getUsage() {
        return "/" + getAliases()[0];
    }
    
    @Override
    public String getDescription() {
        return "Секретная команда";
    }
}

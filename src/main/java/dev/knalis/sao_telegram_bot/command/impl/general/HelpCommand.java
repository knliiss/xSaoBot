package dev.knalis.sao_telegram_bot.command.impl.general;

import dev.knalis.sao_telegram_bot.command.BotCommand;
import dev.knalis.sao_telegram_bot.command.Command;
import dev.knalis.sao_telegram_bot.command.CommandArgs;
import dev.knalis.sao_telegram_bot.model.user.Role;
import dev.knalis.sao_telegram_bot.service.telegram.CommandService;
import dev.knalis.sao_telegram_bot.service.telegram.TelegramSenderService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Lazy;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Command(
        name = "help",
        aliases = {"help", "h", "?"}
)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HelpCommand extends BotCommand {

    CommandService commandService;

    public HelpCommand(TelegramSenderService senderService, @Lazy CommandService commandService) {
        super(senderService);
        this.commandService = commandService;
    }

    @Override
    public void execute(CommandArgs args) {
        var executor = args.getExecutor();
        var text = composeText(executor.getRoles());
        var messageId = args.getMessageId();
        sendMessage(executor.getId(), text);
        deleteMessage(executor.getId(), messageId);
    }
    
    @Override
    public String getUsage() {
        return "/" + getAliases()[0];
    }
    
    @Override
    public String getDescription() {
        return "Справочник по командам";
    }
    
    public String composeText(List<Role> roles) {
        var commands = commandService.getCommands();
        StringBuilder b = new StringBuilder();
        
        b.append("<b>📖 Команды</b>\n")
                .append("<i>Доступные вам команды и их описание</i>\n\n");
        
        for (var command : commands) {
            
            if (!command.isVisible()) continue;
            
            if (command.getAllowedRoles() != null && command.getAllowedRoles().length > 0) {
                boolean allowed = Arrays.stream(command.getAllowedRoles())
                        .anyMatch(roles::contains);
                if (!allowed) continue;
            }
            
            b.append("🔹 <b>")
                    .append(formatAliasesInline(command))
                    .append("</b>\n");
            
            b.append(command.getDescription() != null
                            ? command.getDescription()
                            : "<i>Без описания</i>")
                    .append("\n");
            
            if (command.getUsage() != null && !command.getUsage().isBlank()) {
                b.append("<i>Использование:</i> <code>")
                        .append(command.getUsage())
                        .append("</code>\n");
            }
            
            b.append("\n");
        }
        
        b.append("<i>Команды вводятся без скобок</i>");
        return b.toString();
    }
    
    private String formatAliasesInline(BotCommand command) {
        return Arrays.stream(command.getAliases())
                .map(a -> "/" + a)
                .collect(Collectors.joining(", "));
    }
}

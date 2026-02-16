package dev.knalis.sao_telegram_bot.command.impl.general;

import dev.knalis.sao_telegram_bot.command.BotCommand;
import dev.knalis.sao_telegram_bot.command.Command;
import dev.knalis.sao_telegram_bot.command.CommandArgs;
import dev.knalis.sao_telegram_bot.composer.impl.MenuComposer;
import dev.knalis.sao_telegram_bot.context.ContextPreset;
import dev.knalis.sao_telegram_bot.service.ComposerFactory;
import dev.knalis.sao_telegram_bot.service.telegram.TelegramSenderService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Command(name = "menu"
        , aliases = {"menu", "m", "меню"}
)
public class MenuCommand extends BotCommand {
    
    ComposerFactory composerFactory;
    MenuComposer menuComposer;
    
    public MenuCommand(TelegramSenderService senderService, ComposerFactory composerFactory, MenuComposer menuComposer) {
        super(senderService);
        this.composerFactory = composerFactory;
        this.menuComposer = menuComposer;
    }

    @Override
    public void execute(CommandArgs args) {
        var executor = args.getExecutor();
        var messageId = args.getMessageId();
        var message = composerFactory.render(
                menuComposer,
                ContextPreset.user(executor.getId())
        );
        sendMessage(executor.getId(), message);
        deleteMessage(executor.getId(), messageId);
    }
    
    @Override
    public String getUsage() {
        return "/" + getAliases()[0];
    }
    
    @Override
    public String getDescription() {
        return "Вызов меню";
    }
}

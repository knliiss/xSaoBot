package dev.knalis.sao_telegram_bot.command.impl.general;

import dev.knalis.sao_telegram_bot.command.BotCommand;
import dev.knalis.sao_telegram_bot.command.Command;
import dev.knalis.sao_telegram_bot.command.CommandArgs;
import dev.knalis.sao_telegram_bot.composer.ComposerContext;
import dev.knalis.sao_telegram_bot.service.MenuService;
import dev.knalis.sao_telegram_bot.service.telegram.TelegramSenderService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Command(value = "/menu", description = "Открыть главное меню", aliases = {"/m", "/меню"}, maxArgs = 0)
public class MenuCommand extends BotCommand {

    MenuService menuService;

    public MenuCommand(TelegramSenderService senderService, MenuService menuService) {
        super(senderService);
        this.menuService = menuService;
    }

    @Override
    public void execute(CommandArgs args) {
        var executor = args.getExecutor();
        var messageId = args.getMessageId();
        var context = new ComposerContext(executor.getId());
        var message = menuService.getMenu(context);
        sendMessage(executor.getId(), message);
        deleteMessage(executor.getId(), messageId);
    }
}

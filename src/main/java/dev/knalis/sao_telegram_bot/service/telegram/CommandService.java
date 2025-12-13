package dev.knalis.sao_telegram_bot.service.telegram;

import dev.knalis.sao_telegram_bot.command.BotCommand;
import dev.knalis.sao_telegram_bot.command.Command;
import dev.knalis.sao_telegram_bot.command.CommandArgs;
import dev.knalis.sao_telegram_bot.dto.command.AllowRequest;
import dev.knalis.sao_telegram_bot.dto.command.AllowResponse;
import dev.knalis.sao_telegram_bot.dto.entity.UserCreateDTO;
import dev.knalis.sao_telegram_bot.dto.entity.UserDTO;
import dev.knalis.sao_telegram_bot.model.user.User;
import dev.knalis.sao_telegram_bot.service.intrf.UserService;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommandService {

    @Getter
    List<BotCommand> commands;
    TelegramSenderService senderService;
    HashMap<String, BotCommand> botCommands = new HashMap<>();
    ConsumerService consumerService;
    UserService userService;

    @PostConstruct
    public void init() {
        commands.stream()
                .filter(cmd -> cmd.getClass().getAnnotation(Command.class) != null)
                .forEach(cmd -> {
                    Command cmdAnnotation = cmd.getClass().getAnnotation(Command.class);
                    botCommands.put(cmdAnnotation.value().toLowerCase(), cmd);
                    for (String alias : cmdAnnotation.aliases()) {
                        botCommands.put(alias.toLowerCase(), cmd);
                        log.info("Added alias: {}", alias);
                    }
                });
    }

    public void execute(Update update) {
        if (update == null || !update.hasMessage() || !update.getMessage().hasText()) return;
        long chatId = update.getMessage().getChatId();
        int messageId = update.getMessage().getMessageId();
        var message = update.getMessage();
        var from = message.getFrom();
        String text = update.getMessage().getText().trim();
        if (text.isEmpty()) return;

        var userOptional = userService.findById(chatId);
        User user;
        if (userOptional.isEmpty()) {
            var createDTO= UserCreateDTO.builder()
                    .id(chatId)
                    .username(from.getUserName())
                    .firstName(from.getFirstName())
                    .lastName(from.getLastName())
                    .build();
            user = userService.create(createDTO);
        } else {
            user = userOptional.get();
        }
        var userDTO = new UserDTO(user);

        String[] parts = text.split("\\s+");
        String commandText = parts[0].toLowerCase();
        String[] args = parts.length > 1 ? Arrays.copyOfRange(parts, 1, parts.length) : new String[0];

        BotCommand command = botCommands.get(commandText);
        if (!validateCommand(command, text, args, user, chatId)) return;

        var commandArgs = CommandArgs.builder()
                .messageId(messageId)
                .executor(userDTO)
                .args(args)
                .build();

        try {
            command.execute(commandArgs);
        } catch (Exception e) {
            log.error("Failed to execute command {}", commandText, e);
            senderService.sendMessage(chatId, "❌ Ошибка при выполнении команды. Попробуйте позже.");
        }
    }

    private boolean validateCommand(BotCommand command, String commandText, String[] args, User user, Long chatId) {
        if (!commandText.startsWith("/") ) {
            tryConsumer(chatId, commandText);
            return false;
        }

        if (command == null) {
            log.error("command not found for command {} \n List of valid commands: {}", commandText, botCommands.keySet());
            senderService.sendMessage(chatId, "Неизвестная команда. Используйте /help для списка команд.");
            return false;
        }

        AllowResponse allowResponse = command.isUserAllowed(new AllowRequest(user, commandText, args));
        if (!allowResponse.isAllowed()) {
            command.sendErrorMessage(chatId, allowResponse.getReason());
            return false;
        }
        return true;
    }

    private void tryConsumer(long chatId, String argument) {
        if (consumerService.hasConsumer(chatId)) {
            consumerService.executeConsumer(chatId, argument);
        }
    }

}

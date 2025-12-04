package dev.knalis.sao_telegram_bot.command;

import dev.knalis.sao_telegram_bot.model.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
public class CommandArgs {
    int messageId;
    User executor;
    String[] args;
}

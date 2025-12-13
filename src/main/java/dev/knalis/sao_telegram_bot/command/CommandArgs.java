package dev.knalis.sao_telegram_bot.command;

import dev.knalis.sao_telegram_bot.dto.entity.UserDTO;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
public class CommandArgs {
    int messageId;
    UserDTO executor;
    String[] args;
}

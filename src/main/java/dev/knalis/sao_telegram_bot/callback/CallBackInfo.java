package dev.knalis.sao_telegram_bot.callback;

import dev.knalis.sao_telegram_bot.dto.entity.UserDTO;
import dev.knalis.sao_telegram_bot.model.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CallBackInfo {
    UserDTO user;
    int messageId;
    long timestamp;
}

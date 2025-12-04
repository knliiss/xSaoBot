package dev.knalis.sao_telegram_bot.dto;

import dev.knalis.sao_telegram_bot.model.user.User;
import lombok.Data;

@Data
public class AllowRequest {
    private User user;
    private String command;
    private String[] args;

    public AllowRequest(User user, String commandText, String[] args) {
        this.user = user;
        this.command = commandText;
        this.args = args;
    }
}

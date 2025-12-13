package dev.knalis.sao_telegram_bot.dto.entity;

import dev.knalis.sao_telegram_bot.model.user.Role;
import dev.knalis.sao_telegram_bot.model.user.User;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class UserDTO {
    
    private long id;
    private double balance;
    private short location;
    private String username;
    private String firstName;
    private String lastName;
    private String nickname;
    private Instant createdAt;
    private List<Role> roles;
    
    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.location = user.getLocation();
        this.nickname = user.getNickname();
        this.balance = user.getBalance();
        this.createdAt = user.getCreatedAt();
        this.roles = user.getRoles();
    }
}

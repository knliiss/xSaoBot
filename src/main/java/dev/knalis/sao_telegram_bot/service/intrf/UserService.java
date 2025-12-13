package dev.knalis.sao_telegram_bot.service.intrf;

import dev.knalis.sao_telegram_bot.dto.entity.UserCreateDTO;
import dev.knalis.sao_telegram_bot.model.user.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    
    User create(UserCreateDTO userCreateDTO);
    void updateLocationByUserId(long userId, short location);
    boolean updateNickNameByUserId(long userId, String nickName);
    void linkAdditionalAccount(long userId, String additionalAccount);
    void unlinkAdditionalAccount(long userId, String additionalAccount);
    List<String> getAdditionalAccounts(long userId);
    
    Optional<User> findByUsername(String username);
    Optional<User> findByNickName(String nickName);
    Optional<User> findById(long id);
    List<User> findAllWithAdditionalAccount(String additionalAccount);
    List<User> findAllFromList(List<Long> ids);
    List<User> findAll();
    
    boolean existsByNickName(String nickName);
    
    void withdrawBalance(long userId, double amount);
    void addBalance(long userId, double amount);
    
    
}

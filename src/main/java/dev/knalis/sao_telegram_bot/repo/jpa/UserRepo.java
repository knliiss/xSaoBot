package dev.knalis.sao_telegram_bot.repo.jpa;

import dev.knalis.sao_telegram_bot.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface UserRepo extends JpaRepository<User, Long> {
    boolean existsByNickname(String nickname);
    Optional<User> findByNickname(String nickname);
    List<User> findAll();
    
    Optional<User> findSummaryById(@Param("id") Long id);
    
    Optional<User> findByUsername(String username);
    
    List<User> findAllByAdditionalAccountsContainingIgnoreCase(String additionalAccounts);
}

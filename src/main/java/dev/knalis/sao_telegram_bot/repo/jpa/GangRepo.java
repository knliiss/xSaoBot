package dev.knalis.sao_telegram_bot.repo.jpa;

import dev.knalis.sao_telegram_bot.model.Gang;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GangRepo extends JpaRepository<Gang, Long> {
    boolean existsByName(String name);
    List<Gang> findAll();
}

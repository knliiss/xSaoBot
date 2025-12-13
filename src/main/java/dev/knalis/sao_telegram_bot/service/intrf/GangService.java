package dev.knalis.sao_telegram_bot.service.intrf;

import dev.knalis.sao_telegram_bot.dto.entity.GangActionRequest;
import dev.knalis.sao_telegram_bot.dto.entity.GangCreateDTO;
import dev.knalis.sao_telegram_bot.dto.entity.GangDeleteDTO;
import dev.knalis.sao_telegram_bot.model.Gang;

import java.util.List;
import java.util.Optional;

public interface GangService {
    
    Gang create(GangCreateDTO createDTO);
    Optional<Gang> findById(long id);
    Optional<Gang> findByName(String name);
    List<Gang> findAll();
    boolean existsByName(String name);
    
    void delete(GangDeleteDTO deleteDTO);
    void executeAction(GangActionRequest request);
    
}

package dev.knalis.sao_telegram_bot.model;

import dev.knalis.sao_telegram_bot.model.drop.Drop;
import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "player_drops")
public class PlayerDrops {

    @Id
    private String username;

    private List<Drop> drops;

    public PlayerDrops() {
        drops = new ArrayList<>();
    }
}

package dev.knalis.sao_telegram_bot.composer.intrf;

import dev.knalis.sao_telegram_bot.context.ComposerContext;
import dev.knalis.sao_telegram_bot.dto.telegram.Button;
import dev.knalis.sao_telegram_bot.dto.telegram.ButtonRow;

import java.util.ArrayList;
import java.util.List;

public interface ListableComposer<T> extends Composer {
    
    default List<ButtonRow> buildListOfTypeButtons(
            List<T> items,
            int perRow,
            ComposerContext context
    ) {
        List<ButtonRow> rows = new ArrayList<>();
        ButtonRow currentRow = new ButtonRow();
        
        for (int i = 0; i < items.size(); i++) {
            T item = items.get(i);
            
            Button button = buildItemButton(item, i, context);
            currentRow.add(button);
            
            if (currentRow.size() == perRow) {
                rows.add(currentRow);
                currentRow = new ButtonRow();
            }
        }
        
        if (!currentRow.isEmpty()) {
            rows.add(currentRow);
        }
        
        return rows;
    }
    
    Button buildItemButton(T item, int index, ComposerContext context);
}
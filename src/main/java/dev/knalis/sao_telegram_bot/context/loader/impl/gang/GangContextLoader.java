package dev.knalis.sao_telegram_bot.context.loader.impl.gang;

import dev.knalis.sao_telegram_bot.context.ComposerContext;
import dev.knalis.sao_telegram_bot.context.ContextKey;
import dev.knalis.sao_telegram_bot.context.RequiresContext;
import dev.knalis.sao_telegram_bot.context.loader.ContextLoader;
import dev.knalis.sao_telegram_bot.service.intrf.GangService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@RequiresContext(ContextKey.GANG_ID)
public class GangContextLoader implements ContextLoader {
    
    private final GangService gangService;
    
    @Override
    public void load(ComposerContext context) {
        long gangId = context.get(ContextKey.GANG_ID);
        if (gangId == -1) {
            context.put(provide(), null);
        } else {
            context.put(provide(), gangService.findById(gangId));
        }
    }
    
    @Override
    public ContextKey provide() {
        return ContextKey.GANG;
    }
}

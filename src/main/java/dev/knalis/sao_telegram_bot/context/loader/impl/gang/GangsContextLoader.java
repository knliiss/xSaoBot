package dev.knalis.sao_telegram_bot.context.loader.impl.gang;

import dev.knalis.sao_telegram_bot.context.ComposerContext;
import dev.knalis.sao_telegram_bot.context.ContextKey;
import dev.knalis.sao_telegram_bot.context.loader.ContextLoader;
import dev.knalis.sao_telegram_bot.service.intrf.GangService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GangsContextLoader implements ContextLoader {
    private final GangService gangService;
    
    @Override
    public void load(ComposerContext context) {
        context.put(provide(), gangService.findAll());
    }
    
    @Override
    public ContextKey provide() {
        return ContextKey.GANGS;
    }
}

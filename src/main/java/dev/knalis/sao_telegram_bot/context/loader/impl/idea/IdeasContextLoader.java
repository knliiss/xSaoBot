package dev.knalis.sao_telegram_bot.context.loader.impl.idea;

import dev.knalis.sao_telegram_bot.context.ComposerContext;
import dev.knalis.sao_telegram_bot.context.ContextKey;
import dev.knalis.sao_telegram_bot.context.loader.ContextLoader;
import dev.knalis.sao_telegram_bot.service.intrf.IdeaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IdeasContextLoader implements ContextLoader {
    
    private final IdeaService ideaService;
    
    @Override
    public void load(ComposerContext context) {
        context.put(ContextKey.IDEAS, ideaService.findAll());
    }
    
    @Override
    public ContextKey provide() {
        return ContextKey.IDEAS;
    }
}

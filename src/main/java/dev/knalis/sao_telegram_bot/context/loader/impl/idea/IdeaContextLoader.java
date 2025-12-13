package dev.knalis.sao_telegram_bot.context.loader.impl.idea;

import dev.knalis.sao_telegram_bot.context.ComposerContext;
import dev.knalis.sao_telegram_bot.context.ContextKey;
import dev.knalis.sao_telegram_bot.context.RequiresContext;
import dev.knalis.sao_telegram_bot.context.loader.ContextLoader;
import dev.knalis.sao_telegram_bot.service.intrf.IdeaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@RequiresContext(ContextKey.IDEA_ID)
public class IdeaContextLoader implements ContextLoader {
    
    private final IdeaService ideaService;
    
    @Override
    public void load(ComposerContext context) {
        if (context.contains(ContextKey.IDEA)) return;
        
        var idea = ideaService.findById(context.get(ContextKey.IDEA_ID));
        idea.ifPresent(i -> context.put(ContextKey.IDEA, i));
    }
    
    @Override
    public ContextKey provide() {
        return ContextKey.IDEA;
    }
}


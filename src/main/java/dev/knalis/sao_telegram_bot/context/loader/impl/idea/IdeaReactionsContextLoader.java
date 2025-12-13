package dev.knalis.sao_telegram_bot.context.loader.impl.idea;

import dev.knalis.sao_telegram_bot.context.ComposerContext;
import dev.knalis.sao_telegram_bot.context.ContextKey;
import dev.knalis.sao_telegram_bot.context.RequiresContext;
import dev.knalis.sao_telegram_bot.context.loader.ContextLoader;
import dev.knalis.sao_telegram_bot.service.intrf.IdeaReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@RequiresContext(ContextKey.IDEA_ID)
public class IdeaReactionsContextLoader implements ContextLoader {
    
    private final IdeaReactionService ideaReactionService;
    
    @Override
    public void load(ComposerContext context) {
        var reactions = ideaReactionService.findByIdeaId(context.get(ContextKey.IDEA_ID));
        context.put(ContextKey.IDEA_REACTIONS, reactions);
    }
    
    @Override
    public ContextKey provide() {
        return ContextKey.IDEA_REACTIONS;
    }
}

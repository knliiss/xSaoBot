package dev.knalis.sao_telegram_bot.context.loader;

import dev.knalis.sao_telegram_bot.context.ComposerContext;
import dev.knalis.sao_telegram_bot.context.ContextKey;

public interface ContextLoader {
    
    void load(ComposerContext context);
    
    ContextKey provide();
}

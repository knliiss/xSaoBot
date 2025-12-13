package dev.knalis.sao_telegram_bot.context.loader.impl.messagepack;

import dev.knalis.sao_telegram_bot.context.ComposerContext;
import dev.knalis.sao_telegram_bot.context.ContextKey;
import dev.knalis.sao_telegram_bot.context.loader.ContextLoader;
import dev.knalis.sao_telegram_bot.service.intrf.MessagePackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessagePacksContextLoader implements ContextLoader {
    
    private final MessagePackService messagePackService;
    
    @Override
    public void load(ComposerContext context) {
        context.put(provide(), messagePackService.getAll());
    }
    
    @Override
    public ContextKey provide() {
        return ContextKey.MESSAGEPACKS;
    }
}

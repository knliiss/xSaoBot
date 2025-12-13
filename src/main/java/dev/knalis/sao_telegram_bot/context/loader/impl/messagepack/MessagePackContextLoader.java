package dev.knalis.sao_telegram_bot.context.loader.impl.messagepack;

import dev.knalis.sao_telegram_bot.context.ComposerContext;
import dev.knalis.sao_telegram_bot.context.ContextKey;
import dev.knalis.sao_telegram_bot.context.RequiresContext;
import dev.knalis.sao_telegram_bot.context.loader.ContextLoader;
import dev.knalis.sao_telegram_bot.service.intrf.MessagePackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@RequiresContext(ContextKey.MESSAGEPACK_ID)
public class MessagePackContextLoader implements ContextLoader {
    private final MessagePackService messagePackService;
    
    @Override
    public void load(ComposerContext context) {
        context.put(provide(), messagePackService.getById(context.get(ContextKey.MESSAGEPACK_ID)));
    }
    
    @Override
    public ContextKey provide() {
        return ContextKey.MESSAGEPACK;
    }
}

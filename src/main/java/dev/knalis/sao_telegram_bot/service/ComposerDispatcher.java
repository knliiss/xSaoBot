package dev.knalis.sao_telegram_bot.service;

import dev.knalis.sao_telegram_bot.composer.intrf.Composer;
import dev.knalis.sao_telegram_bot.context.ComposerContext;
import dev.knalis.sao_telegram_bot.context.loader.registry.ContextLoaderRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
@RequiredArgsConstructor
public class ComposerDispatcher {
    
    private final ContextLoaderRegistry contextLoaderRegistry;
    
    public SendMessage render(
            Composer composer,
            ComposerContext context
    ) {
        contextLoaderRegistry.fillContext(composer, context);
        return composer.compose(context);
    }
}

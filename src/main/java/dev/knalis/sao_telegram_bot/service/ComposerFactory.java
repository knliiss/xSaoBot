package dev.knalis.sao_telegram_bot.service;

import dev.knalis.sao_telegram_bot.composer.intrf.Composer;
import dev.knalis.sao_telegram_bot.context.ComposerContext;
import dev.knalis.sao_telegram_bot.context.ContextKey;
import dev.knalis.sao_telegram_bot.context.ContextPreset;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
@RequiredArgsConstructor
public class ComposerFactory {
    
    private final ComposerDispatcher dispatcher;
    
    public SendMessage render(
            Composer composer,
            ContextPreset preset
    ) {
        ComposerContext context = new ComposerContext();
        
        put(context, ContextKey.USER_ID, preset.userId());
        put(context, ContextKey.PAGE, preset.page());
        put(context, ContextKey.BACK_PAGE, preset.backPage());
        put(context, ContextKey.SETTINGS_CATEGORY, preset.settingsCategory());
        
        put(context, ContextKey.IDEA_ID, preset.ideaId());
        put(context, ContextKey.GANG_ID, preset.gangId());
        put(context, ContextKey.MESSAGEPACK_ID, preset.messagePackId());
        
        return dispatcher.render(composer, context);
    }
    
    private <T> void put(ComposerContext ctx, ContextKey key, T value) {
        if (value != null) {
            ctx.put(key, value);
        }
    }
}
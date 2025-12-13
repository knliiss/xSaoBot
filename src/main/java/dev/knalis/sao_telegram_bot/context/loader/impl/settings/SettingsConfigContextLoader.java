package dev.knalis.sao_telegram_bot.context.loader.impl.settings;

import dev.knalis.sao_telegram_bot.context.ComposerContext;
import dev.knalis.sao_telegram_bot.context.ContextKey;
import dev.knalis.sao_telegram_bot.context.RequiresContext;
import dev.knalis.sao_telegram_bot.context.loader.ContextLoader;
import dev.knalis.sao_telegram_bot.service.intrf.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@RequiresContext(ContextKey.USER_ID)
public class SettingsConfigContextLoader implements ContextLoader {
    
    private final UserService userService;
    
    @Override
    public void load(ComposerContext context) {
        context.put(provide(), userService.getActiveSettingsConfig(context.get(ContextKey.USER_ID)));
    }
    
    @Override
    public ContextKey provide() {
        return ContextKey.SETTINGS_CONFIG;
    }
}

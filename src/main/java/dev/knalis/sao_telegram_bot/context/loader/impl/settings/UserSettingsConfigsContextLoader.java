package dev.knalis.sao_telegram_bot.context.loader.impl.settings;

import dev.knalis.sao_telegram_bot.context.ComposerContext;
import dev.knalis.sao_telegram_bot.context.ContextKey;
import dev.knalis.sao_telegram_bot.context.RequiresContext;
import dev.knalis.sao_telegram_bot.context.loader.ContextLoader;
import dev.knalis.sao_telegram_bot.service.intrf.SettingsConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@RequiresContext(ContextKey.USER_ID)
public class UserSettingsConfigsContextLoader implements ContextLoader {
    
    
    private final SettingsConfigService settingsConfigService;
    
    @Override
    public void load(ComposerContext context) {
        Long userId = context.get(ContextKey.USER_ID);
        context.put(provide(), settingsConfigService.getAllConfigs(userId));
    }
    
    @Override
    public ContextKey provide() {
        return ContextKey.USER_SETTINGS_CONFIGS;
    }
}

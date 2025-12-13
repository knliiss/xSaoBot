package dev.knalis.sao_telegram_bot.context.loader.impl.user;

import dev.knalis.sao_telegram_bot.context.ComposerContext;
import dev.knalis.sao_telegram_bot.context.ContextKey;
import dev.knalis.sao_telegram_bot.context.RequiresContext;
import dev.knalis.sao_telegram_bot.context.loader.ContextLoader;
import dev.knalis.sao_telegram_bot.service.intrf.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiresContext(ContextKey.USER_ID)
@RequiredArgsConstructor
public class UserContextLoader implements ContextLoader {
    
    private final UserService userService;
    
    @Override
    public void load(ComposerContext context) {
        if (context.contains(ContextKey.USER)) return;
        
        var userOpt = userService.findById(context.get(ContextKey.USER_ID));
        userOpt.ifPresent(user -> context.put(ContextKey.USER, user));
    }
    
    @Override
    public ContextKey provide() {
        return ContextKey.USER;
    }
}

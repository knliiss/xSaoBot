package dev.knalis.sao_telegram_bot.context.loader.impl.user;

import dev.knalis.sao_telegram_bot.context.ComposerContext;
import dev.knalis.sao_telegram_bot.context.ContextKey;
import dev.knalis.sao_telegram_bot.context.loader.ContextLoader;
import dev.knalis.sao_telegram_bot.service.intrf.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsersContextLoader implements ContextLoader {
    
    private final UserService userService;
    
    @Override
    public void load(ComposerContext context) {
        context.put(ContextKey.USERS, userService.findAll());
    }
    
    @Override
    public ContextKey provide() {
        return ContextKey.USERS;
    }
}

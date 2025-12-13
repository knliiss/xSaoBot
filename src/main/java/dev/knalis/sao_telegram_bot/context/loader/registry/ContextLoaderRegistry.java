package dev.knalis.sao_telegram_bot.context.loader.registry;

import dev.knalis.sao_telegram_bot.composer.intrf.Composer;
import dev.knalis.sao_telegram_bot.context.ComposerContext;
import dev.knalis.sao_telegram_bot.context.ContextKey;
import dev.knalis.sao_telegram_bot.context.RequiresContext;
import dev.knalis.sao_telegram_bot.context.loader.ContextLoader;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class ContextLoaderRegistry {
    
    private final List<ContextLoader> loaders;
    private final Map<ContextKey, ContextLoader> loaderByKey = new EnumMap<>(ContextKey.class);
    
    @PostConstruct
    void init() {
        for (ContextLoader loader : loaders) {
            loaderByKey.put(loader.provide(), loader);
        }
    }
    
    public void fillContext(Composer composer, ComposerContext ctx) {
        
        RequiresContext requiresKeys =
                composer.getClass().getAnnotation(RequiresContext.class);
        
        if (requiresKeys == null) return;
        
        Set<ContextKey> visited = EnumSet.noneOf(ContextKey.class);
        
        for (ContextKey key : requiresKeys.value()) {
            resolve(key, ctx, visited);
        }
    }
    
    private void resolve(
            ContextKey key,
            ComposerContext ctx,
            Set<ContextKey> visited
    ) {
        if (ctx.contains(key)) return;
        
        if (!visited.add(key)) {
            throw new IllegalStateException(
                    "Cyclic context dependency detected at " + key
            );
        }
        
        ContextLoader loader = loaderByKey.get(key);
        if (loader == null) {
            throw new IllegalStateException(
                    "No ContextLoader registered for key " + key
            );
        }
        
        RequiresContext requiresContext =
                loader.getClass().getAnnotation(RequiresContext.class);
        
        if (requiresContext != null) {
            for (ContextKey dep : requiresContext.value()) {
                if (!ctx.contains(dep)) {
                    resolve(dep, ctx, visited);
                }
            }
        }
        
        loader.load(ctx);
    }
}
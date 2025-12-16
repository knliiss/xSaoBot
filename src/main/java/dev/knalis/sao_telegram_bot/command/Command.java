package dev.knalis.sao_telegram_bot.command;

import dev.knalis.sao_telegram_bot.model.user.Role;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface Command {
    String value();
    String description() default "";
    String[] aliases() default {};
    Role[] allowedRoles() default {Role.USER};
    boolean visible() default true;
    int minArgs() default 0;
    int maxArgs() default Integer.MAX_VALUE;
}

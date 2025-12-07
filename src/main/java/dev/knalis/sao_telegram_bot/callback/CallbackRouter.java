package dev.knalis.sao_telegram_bot.callback;

import dev.knalis.sao_telegram_bot.callback.annotation.CallBackController;
import dev.knalis.sao_telegram_bot.callback.annotation.CallBackMethod;
import dev.knalis.sao_telegram_bot.callback.annotation.PathVariable;
import dev.knalis.sao_telegram_bot.service.crud.impl.UserService;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CallbackRouter {

    ApplicationContext context;
    UserService userService;
    List<Route> routes = new ArrayList<>();

    @PostConstruct
    public void init() {
        for (Object bean : context.getBeansWithAnnotation(CallBackController.class).values()) {
            Class<?> targetClass = AopUtils.getTargetClass(bean);
            String base = targetClass.getAnnotation(CallBackController.class).value();

            for (Method declared : targetClass.getDeclaredMethods()) {
                if (declared.isAnnotationPresent(CallBackMethod.class)) {
                    String path = base + declared.getAnnotation(CallBackMethod.class).value();

                    List<String> variables = new ArrayList<>();
                    Matcher m = Pattern.compile("\\{([^/]+)\\}").matcher(path);
                    while (m.find()) {
                        variables.add(m.group(1));
                    }
                    if (!variables.isEmpty()) {
                        for (int i = 0; i < variables.size() - 1; i++) {
                            if ("page".equals(variables.get(i))) {
                                throw new IllegalStateException(
                                        "@PathVariable {page} must be at the end of the path: " + path
                                );
                            }
                        }
                    }

                    String regex = path.replaceAll("\\{[^/]+\\}", "([^/]+)");
                    String anchored = "^" + regex + "$";
                    Pattern pattern = Pattern.compile(anchored);

                    // Resolve invokable method on the bean instance class (handles proxies)
                    Method invokable = null;
                    Class<?> beanClass = bean.getClass();
                    try {
                        invokable = beanClass.getMethod(declared.getName(), declared.getParameterTypes());
                    } catch (NoSuchMethodException e) {
                        for (Method mth : beanClass.getMethods()) {
                            if (mth.getName().equals(declared.getName()) && mth.getParameterCount() == declared.getParameterCount()) {
                                invokable = mth;
                                break;
                            }
                        }
                    }
                    if (invokable == null) invokable = declared;

                    routes.add(new Route(regex, pattern, declared, invokable, bean));
                    log.debug("Registered callback route: {} -> {}#{}", anchored, bean.getClass().getSimpleName(), declared.getName());
                }
            }
        }
        log.info("CallbackRouter initialized with {} routes", routes.size());
    }

    public void dispatch(Update update) {
        String callback = update.getCallbackQuery().getData();
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        int messageId = update.getCallbackQuery().getMessage().getMessageId();
        long timeStamp = System.currentTimeMillis();
        var user = userService.findById(chatId);
        var info = CallBackInfo.builder()
                .user(user)
                .messageId(messageId)
                .timestamp(timeStamp)
                .build();


        for (Route route : routes) {
            Matcher matcher = route.pattern().matcher(callback);
            if (matcher.matches()) {
                invoke(route, matcher, info);
                return;
            }
        }
        log.warn("No callback found for callback: {}", callback);
    }

    private void invoke(Route route, Matcher matcher, CallBackInfo info) {
        Method declared = route.declared();
        Method invokable = route.invokable();
        Parameter[] params = declared.getParameters();
        Object[] args = new Object[params.length];
        int groupIndex = 1;

        for (int i = 0; i < params.length; i++) {
            Parameter p = params[i];

            if (p.isAnnotationPresent(PathVariable.class)) {
                String value = matcher.group(groupIndex++);
                Class<?> type = p.getType();

                Object converted = switch (type.getSimpleName()) {
                    case "int", "Integer" -> Integer.parseInt(value);
                    case "long", "Long" -> Long.parseLong(value);
                    case "double", "Double" -> Double.parseDouble(value);
                    case "boolean", "Boolean" -> Boolean.parseBoolean(value);
                    default -> value;
                };

                args[i] = converted;
            } else if (p.getType().equals(CallBackInfo.class)) {
                args[i] = info;
            } else {
                args[i] = null;
            }
        }

        try {
            invokable.setAccessible(true);
            invokable.invoke(route.controller(), args);
            return;
        } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
            // try to find a compatible method on the controller and invoke it
            try {
                Method compatible = findCompatibleMethod(route.controller().getClass(), declared.getName(), args);
                if (compatible != null) {
                    compatible.setAccessible(true);
                    compatible.invoke(route.controller(), args);
                    return;
                }
            } catch (Exception ex) {
                log.error("Fallback invocation failed", ex);
            }

            try {
                String ctrlClass = route.controller().getClass().getName();
                String mName = declared.getName();
                StringBuilder argStr = new StringBuilder();
                for (Object a : args) argStr.append(a).append(",");
                log.error("Failed to invoke callback handler {}#{} for route {} with args [{}]", ctrlClass, mName, route.regex(), argStr.toString(), e);
            } catch (Exception ex) {
                log.error("Failed to invoke callback handler and also failed to log details", e);
            }
        }
    }

    private Method findCompatibleMethod(Class<?> cls, String name, Object[] args) {
        for (Method m : cls.getMethods()) {
            if (!m.getName().equals(name)) continue;
            Class<?>[] pts = m.getParameterTypes();
            if (pts.length != args.length) continue;
            boolean ok = true;
            for (int i = 0; i < pts.length; i++) {
                if (args[i] == null) continue;
                if (!isAssignable(pts[i], args[i].getClass())) { ok = false; break; }
            }
            if (ok) return m;
        }
        return null;
    }

    private boolean isAssignable(Class<?> paramType, Class<?> argClass) {
        if (paramType.isPrimitive()) {
            if (paramType == int.class && argClass == Integer.class) return true;
            if (paramType == long.class && argClass == Long.class) return true;
            if (paramType == double.class && argClass == Double.class) return true;
            if (paramType == boolean.class && argClass == Boolean.class) return true;
            return false;
        }
        return paramType.isAssignableFrom(argClass);
    }

    private record Route(String regex, Pattern pattern, Method declared, Method invokable, Object controller) {}

}

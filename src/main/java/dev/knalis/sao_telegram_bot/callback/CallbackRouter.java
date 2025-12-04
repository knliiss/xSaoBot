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
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

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
            Class<?> clazz = bean.getClass();
            String base = clazz.getAnnotation(CallBackController.class).value();

            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(CallBackMethod.class)) {
                    String path = base + method.getAnnotation(CallBackMethod.class).value();

                    List<String> variables = new ArrayList<>();
                    Matcher m = Pattern.compile("\\{([^/]+)\\}").matcher(path);
                    while (m.find()) {
                        variables.add(m.group(1));
                    }
                    if (!variables.isEmpty()) {
                        String lastVar = variables.get(variables.size() - 1);
                        for (int i = 0; i < variables.size() - 1; i++) {
                            if ("page".equals(variables.get(i))) {
                                throw new IllegalStateException(
                                        "@PathVariable {page} must be at the end of the path: " + path
                                );
                            }
                        }
                    }

                    String regex = path.replaceAll("\\{[^/]+\\}", "([^/]+)");
                    routes.add(new Route(regex, Pattern.compile(regex), method, bean));
                }
            }
        }
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
        Method method = route.method();
        Parameter[] params = method.getParameters();
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
            method.setAccessible(true);
            method.invoke(route.controller(), args);
        } catch (Exception e) {
            log.error("Обработчик не нашел нужный метод для сallback: {}", route.regex());
        }
    }

    private record Route(String regex, Pattern pattern, Method method, Object controller) {}

}

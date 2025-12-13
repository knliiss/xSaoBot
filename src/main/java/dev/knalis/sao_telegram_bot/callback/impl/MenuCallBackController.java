package dev.knalis.sao_telegram_bot.callback.impl;

import dev.knalis.sao_telegram_bot.callback.CallBackInfo;
import dev.knalis.sao_telegram_bot.callback.AbstractCallBackController;
import dev.knalis.sao_telegram_bot.callback.annotation.CallBackController;
import dev.knalis.sao_telegram_bot.callback.annotation.CallBackMethod;
import dev.knalis.sao_telegram_bot.callback.annotation.PathVariable;
import dev.knalis.sao_telegram_bot.composer.ComposerContext;
import dev.knalis.sao_telegram_bot.composer.ContextKey;
import dev.knalis.sao_telegram_bot.service.MenuService;
import dev.knalis.sao_telegram_bot.service.telegram.TelegramSenderService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@CallBackController("menu/{userId}")
@Component
public class MenuCallBackController extends AbstractCallBackController {

    MenuService menuService;

    public MenuCallBackController(TelegramSenderService senderService, MenuService menuService) {
        super(senderService);
        this.menuService = menuService;
    }

    // base menu: menu/{userId}
    @CallBackMethod("")
    public void mainMenu(@PathVariable("userId") long userId, CallBackInfo info) {
        safeExecute(info.getUser().getId(), () -> {
            var context = new ComposerContext(userId);
            var message = menuService.getMenu(context);
            editMessage(info.getUser().getId(), info.getMessageId(), message);
        }, "❌ Не удалось открыть меню. Попробуйте позже.");
    }

    @CallBackMethod("/user")
    public void userMenu(@PathVariable("userId") long userId, CallBackInfo info) {
        safeExecute(info.getUser().getId(), () -> {
            var context = new ComposerContext(userId);
            var message = menuService.getUserMenu(context);
            editMessage(info.getUser().getId(), info.getMessageId(), message);
        }, "❌ Не удалось открыть профиль. Попробуйте позже.");
    }

    @CallBackMethod("/user/location")
    public void locationMenu(@PathVariable("userId") long userId, CallBackInfo info) {
        safeExecute(info.getUser().getId(), () -> {
            var context = new ComposerContext(userId);
            var message = menuService.getLocationMenu(context);
            editMessage(info.getUser().getId(), info.getMessageId(), message);
        }, "❌ Не удалось открыть локации. Попробуйте позже.");
    }

    @CallBackMethod("/user/account")
    public void accountMenu(@PathVariable("userId") long userId, CallBackInfo info) {
        safeExecute(info.getUser().getId(), () -> {
            var context = new ComposerContext(userId);
            context.put("page", "1");
            var message = menuService.getAdditionalAccountsMenu(context);
            editMessage(info.getUser().getId(), info.getMessageId(), message);
        }, "❌ Не удалось открыть аккаунты. Попробуйте позже.");
    }

    @CallBackMethod("/user/account/{page}")
    public void accountMenuPage(@PathVariable("userId") long userId, @PathVariable("page") int page, CallBackInfo info) {
        safeExecute(info.getUser().getId(), () -> {
            var context = new ComposerContext(userId);
            context.put("page", Integer.toString(Math.max(1, page)));
            var message = menuService.getAdditionalAccountsMenu(context);
            editMessage(info.getUser().getId(), info.getMessageId(), message);
        }, "❌ Не удалось загрузить страницу аккаунтов. Попробуйте позже.");
    }

    @CallBackMethod("/settings/{category}")
    public void settingsMenu(@PathVariable("userId") long userId, @PathVariable("category") String category, CallBackInfo info) {
        safeExecute(info.getUser().getId(), () -> {
            var context = new ComposerContext(userId);
            context.put("category", category);
            var message = menuService.getSettingsMenu(context);
            editMessage(info.getUser().getId(), info.getMessageId(), message);
        }, "❌ Не удалось открыть настройки. Попробуйте позже.");
    }

    @CallBackMethod("/messagepack/{page}")
    public void messagePackList(@PathVariable("userId") long userId, @PathVariable("page") int page, CallBackInfo info) {
        safeExecute(info.getUser().getId(), () -> {
            var context = new ComposerContext(userId);
            context.put("page", Integer.toString(Math.max(1, page)));
            var message = menuService.getMessagePackMenu(context);
            editMessage(info.getUser().getId(), info.getMessageId(), message);
        }, "❌ Не удалось открыть магазин. Попробуйте позже.");
    }

    @CallBackMethod("/messagepack/{messagePackId}/{backPage}")
    public void messagePackDetails(@PathVariable("userId") long userId, @PathVariable("messagePackId") String messagePackId, @PathVariable("backPage") String backPage, CallBackInfo info) {
        safeExecute(info.getUser().getId(), () -> {
            var context = new ComposerContext(userId);
            context.put(ContextKey.PAGE, backPage);
            context.put("messagePackId", messagePackId);
            var message = menuService.getMessagePackMenu(context, messagePackId);
            editMessage(info.getUser().getId(), info.getMessageId(), message);
        }, "❌ Не удалось открыть пакет сообщений. Попробуйте позже.");
    }

    @CallBackMethod("/gang")
    public void gangMenu(@PathVariable("userId") long userId, CallBackInfo info) {
        safeExecute(info.getUser().getId(), () -> {
            var context = new ComposerContext(userId);
            var message = menuService.getGangMenu(context);
            editMessage(info.getUser().getId(), info.getMessageId(), message);
        }, "❌ Не удалось открыть банды. Попробуйте позже.");
    }

    @CallBackMethod("/idea/{page}")
    public void ideaMenu(@PathVariable("userId") long userId, @PathVariable("page") int page, CallBackInfo info) {
        safeExecute(info.getUser().getId(), () -> {
            var context = new ComposerContext(userId);
            context.put("page", Integer.toString(Math.max(1, page)));
            var message = menuService.getIdeaMenu(context);
            editMessage(info.getUser().getId(), info.getMessageId(), message);
        }, "❌ Не удалось открыть идеи. Попробуйте позже.");
    }
    
    @CallBackMethod("/idea/{ideaId}/{backpage}")
    public void ideaDetailMenu(@PathVariable("userId") long userId, @PathVariable("ideaId") long ideaId, @PathVariable("backPage") int backPage, CallBackInfo info) {
        safeExecute(info.getUser().getId(), () -> {
                    var context = new ComposerContext(userId);
                    context.put(ContextKey.BACK_CALLBACK_URL, "menu/" + userId + "/idea/" + backPage);
                    context.put(ContextKey.PAGE, Integer.toString(Math.max(1, backPage)));
                    context.put("ideaId", Long.toString(ideaId));
                    var message = menuService.getIdeaDeatilMenu(context);
                    editMessage(info.getUser().getId(), info.getMessageId(), message);
                },"❌ Не удалось открыть идею. Попробуйте позже.");
    }

    @CallBackMethod("/reminder")
    public void reminderMenu(@PathVariable("userId") long userId, CallBackInfo info) {
        safeExecute(info.getUser().getId(), () -> {
            var context = new ComposerContext(userId);
            var message = menuService.getReminderMenu(context);
            context.put(ContextKey.BACK_CALLBACK_URL, "menu/" + userId + "/user");
            editMessage(info.getUser().getId(), info.getMessageId(), message);
        }, "❌ Не удалось открыть напоминания. Попробуйте позже.");
    }
}

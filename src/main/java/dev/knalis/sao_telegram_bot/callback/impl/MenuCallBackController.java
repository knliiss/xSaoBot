package dev.knalis.sao_telegram_bot.callback.impl;

import dev.knalis.sao_telegram_bot.callback.AbstractCallBackController;
import dev.knalis.sao_telegram_bot.callback.CallBackInfo;
import dev.knalis.sao_telegram_bot.callback.annotation.CallBackController;
import dev.knalis.sao_telegram_bot.callback.annotation.CallBackMethod;
import dev.knalis.sao_telegram_bot.callback.annotation.PathVariable;
import dev.knalis.sao_telegram_bot.composer.impl.*;
import dev.knalis.sao_telegram_bot.context.ContextPreset;
import dev.knalis.sao_telegram_bot.model.user.settings.NotificationCategory;
import dev.knalis.sao_telegram_bot.service.ComposerFactory;
import dev.knalis.sao_telegram_bot.service.intrf.UserService;
import dev.knalis.sao_telegram_bot.service.telegram.TelegramSenderService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@CallBackController("menu/{userId}")
@Component
public class MenuCallBackController extends AbstractCallBackController {
    
    ComposerFactory composerFactory;
    MenuComposer menuComposer;
    UserMenuComposer userMenuComposer;
    IdeaMenuComposer ideaMenuComposer;
    IdeaDetailMenuComposer ideaDetailMenuComposer;
    AdditionalAccountsMenuComposer additionalAccountsMenuComposer;
    SettingsMenuComposer settingsMenuComposer;
    GangMenuComposer gangMenuComposer;
    LocationMenuComposer locationMenuComposer;
    MessagePackDetailsComposer messagePackDetailsComposer;
    MessagePackMenuComposer messagePackMenuComposer;
    ReminderMenuComposer reminderMenuComposer;
    private final UserService userService;
    
    
    public MenuCallBackController(TelegramSenderService senderService, ComposerFactory composerFactory, MenuComposer menuComposer, UserMenuComposer userMenuComposer, IdeaMenuComposer ideaMenuComposer, IdeaDetailMenuComposer ideaDetailMenuComposer, AdditionalAccountsMenuComposer additionalAccountsMenuComposer, SettingsMenuComposer settingsMenuComposer, GangMenuComposer gangMenuComposer, LocationMenuComposer locationMenuComposer, MessagePackDetailsComposer messagePackDetailsComposer, MessagePackMenuComposer messagePackMenuComposer, ReminderMenuComposer reminderMenuComposer, UserService userService) {
        super(senderService);
        this.composerFactory = composerFactory;
        this.menuComposer = menuComposer;
        this.userMenuComposer = userMenuComposer;
        this.ideaMenuComposer = ideaMenuComposer;
        this.ideaDetailMenuComposer = ideaDetailMenuComposer;
        this.additionalAccountsMenuComposer = additionalAccountsMenuComposer;
        this.settingsMenuComposer = settingsMenuComposer;
        this.gangMenuComposer = gangMenuComposer;
        this.locationMenuComposer = locationMenuComposer;
        this.messagePackDetailsComposer = messagePackDetailsComposer;
        this.messagePackMenuComposer = messagePackMenuComposer;
        this.reminderMenuComposer = reminderMenuComposer;
        this.userService = userService;
    }

    // base menu: menu/{userId}
    @CallBackMethod("")
    public void mainMenu(@PathVariable("userId") long userId, CallBackInfo info) {
        safeExecute(info.getUser().getId(), () -> {
            editMessage(userId, info.getMessageId(), composerFactory.render(
                    menuComposer,
                    ContextPreset.user(userId)
            ));
        }, "❌ Не удалось открыть меню. Попробуйте позже.");
    }

    @CallBackMethod("/user")
    public void userMenu(@PathVariable("userId") long userId, CallBackInfo info) {
        safeExecute(info.getUser().getId(), () -> {
            editMessage(userId, info.getMessageId(), composerFactory.render(
                    userMenuComposer,
                    ContextPreset.user(userId)
            ));
        }, "❌ Не удалось открыть профиль. Попробуйте позже.");
    }

    @CallBackMethod("/user/location")
    public void locationMenu(@PathVariable("userId") long userId, CallBackInfo info) {
        safeExecute(info.getUser().getId(), () -> {
            editMessage(userId, info.getMessageId(), composerFactory.render(
                    locationMenuComposer,
                    ContextPreset.user(userId)
            ));
        }, "❌ Не удалось открыть локации. Попробуйте позже.");
    }
    
    @CallBackMethod("/user/account/{page}")
    public void accountMenu(@PathVariable("userId") long userId, @PathVariable("page") int page, CallBackInfo info) {
        safeExecute(info.getUser().getId(), () -> {
            editMessage(userId, info.getMessageId(), composerFactory.render(
                    additionalAccountsMenuComposer,
                    ContextPreset.user(userId).page(Math.max(1, page))
            ));
        }, "❌ Не удалось открыть аккаунты. Попробуйте позже.");
    }
    
    @CallBackMethod("/settings/{category}")
    public void settingsMenu(@PathVariable("userId") long userId, @PathVariable("category") String category, CallBackInfo info) {
        safeExecute(info.getUser().getId(), () -> {
            editMessage(userId, info.getMessageId(), composerFactory.render(
                    settingsMenuComposer,
                    ContextPreset.user(userId).settingsCategory(NotificationCategory.valueOf(category.toUpperCase()))
            ));
        }, "❌ Не удалось открыть настройки. Попробуйте позже.");
    }

    @CallBackMethod("/messagepack/{page}")
    public void messagePackList(@PathVariable("userId") long userId, @PathVariable("page") int page, CallBackInfo info) {
        safeExecute(info.getUser().getId(), () -> {
            editMessage(userId, info.getMessageId(), composerFactory.render(
                    messagePackMenuComposer,
                    ContextPreset.user(userId).page(Math.max(1, page)
                    )));
        }, "❌ Не удалось открыть магазин. Попробуйте позже.");
    }

    @CallBackMethod("/messagepack/{messagePackId}/{backPage}")
    public void messagePackDetails(@PathVariable("userId") long userId, @PathVariable("messagePackId") String messagePackId, @PathVariable("backPage") String backPage, CallBackInfo info) {
        safeExecute(info.getUser().getId(), () -> {
            editMessage(userId, info.getMessageId(), composerFactory.render(
                    messagePackDetailsComposer,
                    ContextPreset.user(userId).messagePack(messagePackId).backPage(Integer.parseInt(backPage)
                    )));
        }, "❌ Не удалось открыть пакет сообщений. Попробуйте позже.");
    }

    @CallBackMethod("/gang")
    public void gangMenu(@PathVariable("userId") long userId, CallBackInfo info) {
        safeExecute(info.getUser().getId(), () -> {
            var gang = userService.findById(userId).get().getGang();
            var gangId = gang != null ? gang.getId() : -1;
            editMessage(userId, info.getMessageId(), composerFactory.render(
                    gangMenuComposer,
                    ContextPreset.user(userId).gang(gangId)
            ));
        }, "❌ Не удалось открыть банды. Попробуйте позже.");
    }

    @CallBackMethod("/idea/{page}")
    public void ideaMenu(@PathVariable("userId") long userId, @PathVariable("page") int page, CallBackInfo info) {
        safeExecute(info.getUser().getId(), () -> {
            editMessage(userId, info.getMessageId(), composerFactory.render(
                    ideaMenuComposer,
                    ContextPreset.user(userId).page(Math.max(1, page))
            ));
        }, "❌ Не удалось открыть идеи. Попробуйте позже.");
    }
    
    @CallBackMethod("/idea/{ideaId}/{backpage}")
    public void ideaDetailMenu(@PathVariable("userId") long userId, @PathVariable("ideaId") long ideaId, @PathVariable("backPage") int backPage, CallBackInfo info) {
        safeExecute(info.getUser().getId(), () -> {
            editMessage(userId, info.getMessageId(), composerFactory.render(
                    ideaDetailMenuComposer,
                    ContextPreset.user(userId).idea(ideaId).backPage(backPage)
            ));
                },"❌ Не удалось открыть идею. Попробуйте позже.");
    }

    @CallBackMethod("/reminder")
    public void reminderMenu(@PathVariable("userId") long userId, CallBackInfo info) {
        safeExecute(info.getUser().getId(), () -> {
            editMessage(userId, info.getMessageId(), composerFactory.render(
                    reminderMenuComposer,
                    ContextPreset.user(userId)
            ));
        }, "❌ Не удалось открыть напоминания. Попробуйте позже.");
    }
}

package dev.knalis.sao_telegram_bot.service;

import dev.knalis.sao_telegram_bot.composer.ComposerContext;
import dev.knalis.sao_telegram_bot.composer.impl.*;
import dev.knalis.sao_telegram_bot.composer.ContextKey;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class MenuService {
    MenuComposer menuComposer;
    UserMenuComposer userMenuComposer;
    IdeaMenuComposer ideaMenuComposer;
    LocationMenuComposer locationMenuComposer;
    ReminderMenuComposer reminderMenuComposer;
    SettingsMenuComposer settingsMenuComposer;
    MessagePackMenuComposer messagePackMenuComposer;
    MessagePackDetailsComposer messagePackDetailsComposer;
    GangMenuComposer gangMenuComposer;
    AdditionalAccountsMenuComposer additionalAccountsMenuComposer;

    public SendMessage getMenu(ComposerContext context) {
        return menuComposer.compose(context);
    }

    public SendMessage getUserMenu(ComposerContext context) {
        return userMenuComposer.compose(context);
    }

    public SendMessage getLocationMenu(ComposerContext context) {
        return locationMenuComposer.compose(context);
    }

    public SendMessage getReminderMenu(ComposerContext context) {
        return reminderMenuComposer.compose(context);
    }

    public SendMessage getSettingsMenu(ComposerContext context) {
        return settingsMenuComposer.compose(context);
    }

    public SendMessage getMessagePackMenu(ComposerContext context) {
        return messagePackMenuComposer.compose(context);
    }

    public SendMessage getMessagePackMenu(ComposerContext context, String messagePackId) {
        context.put("messagePackId", messagePackId);
        return messagePackDetailsComposer.compose(context);
    }

    public SendMessage getGangMenu(ComposerContext context) {
        return gangMenuComposer.compose(context);
    }

    public SendMessage getAllGangMenu(ComposerContext context) {
        return gangMenuComposer.compose(context);
    }

    public SendMessage getIdeaMenu(ComposerContext context) {
        return ideaMenuComposer.compose(context);
    }
    
    public SendMessage getAdditionalAccountsMenu(ComposerContext context) {
        return additionalAccountsMenuComposer.compose(context);
    }
}

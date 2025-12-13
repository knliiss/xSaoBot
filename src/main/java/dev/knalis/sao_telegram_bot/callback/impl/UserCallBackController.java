package dev.knalis.sao_telegram_bot.callback.impl;

import dev.knalis.sao_telegram_bot.callback.AbstractCallBackController;
import dev.knalis.sao_telegram_bot.callback.CallBackInfo;
import dev.knalis.sao_telegram_bot.callback.annotation.CallBackController;
import dev.knalis.sao_telegram_bot.callback.annotation.CallBackMethod;
import dev.knalis.sao_telegram_bot.callback.annotation.PathVariable;
import dev.knalis.sao_telegram_bot.service.ComposerFactory;
import dev.knalis.sao_telegram_bot.service.intrf.UserService;
import dev.knalis.sao_telegram_bot.service.telegram.ConsumerService;
import dev.knalis.sao_telegram_bot.service.telegram.TelegramSenderService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@CallBackController("user/{chatId}")
@Slf4j
public class UserCallBackController extends AbstractCallBackController {

    ConsumerService consumerService;
    ComposerFactory menuService;
    UserService userService;
    MenuCallBackController menuCallBackController;
    
    public UserCallBackController(TelegramSenderService senderService, ConsumerService consumerService, ComposerFactory menuService, UserService userService, MenuCallBackController menuCallBackController) {
        super(senderService);
        this.consumerService = consumerService;
        this.menuService = menuService;
        this.userService = userService;
        this.menuCallBackController = menuCallBackController;
    }
    

    @CallBackMethod("/location/set/{locationId}")
    private void setLocation(@PathVariable("chatId") long chatId, @PathVariable("locationId") short locationId, CallBackInfo info) {
        safeExecute(chatId, () -> {
            userService.updateLocationByUserId(chatId, locationId);
            menuCallBackController.userMenu(chatId, info);

        }, "❌ Не удалось изменить локацию. Попробуйте позже.");
    }

    @CallBackMethod("/account/link/main")
    private void linkMain(@PathVariable("chatId") long chatId, CallBackInfo info) {
        var userId = info.getUser().getId();
        int tempMessageId = sendMessage(userId, "✏️ Введите новый никнейм. Для отмены используйте /cancel.");
        
        Consumer<String> consumer = input -> {
            safeExecute(userId, () -> {
                userService.updateNickNameByUserId(chatId, input);
            }, "❌ Не удалось изменить никнейм. Никнейм может быть уже занят.");
            
            menuCallBackController.userMenu(chatId, info);
            deleteMessage(userId, tempMessageId);
        };
        
        consumerService.addConsumer(userId, consumer);
    }

    @CallBackMethod("/account/link/additional/{page}")
    private void linkAdditional(@PathVariable("chatId") long chatId, @PathVariable("page") int page , CallBackInfo info) {
        long userId = info.getUser().getId();
        int promptId = sendMessage(userId, "➕ Введите ник дополнительного аккаунта. Для отмены /cancel.");
        consumerService.addConsumer(userId, input -> {
            userService.linkAdditionalAccount(chatId, input);
            menuCallBackController.accountMenu(chatId, page, info);
            deleteMessage(userId, promptId);
        });
    }

    @CallBackMethod("/account/unlink/{username}/{page}")
    private void unlinkAdditional(@PathVariable("chatId") long chatId, @PathVariable("username") String username, @PathVariable("page") int page, CallBackInfo info) {
        userService.unlinkAdditionalAccount(chatId, username);
        menuCallBackController.accountMenu(chatId, page, info);
    }
}

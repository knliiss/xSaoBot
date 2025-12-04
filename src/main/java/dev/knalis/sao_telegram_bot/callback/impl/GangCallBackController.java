package dev.knalis.sao_telegram_bot.callback.impl;

import dev.knalis.sao_telegram_bot.callback.CallBackInfo;
import dev.knalis.sao_telegram_bot.callback.AbstractCallBackController;
import dev.knalis.sao_telegram_bot.callback.annotation.CallBackController;
import dev.knalis.sao_telegram_bot.callback.annotation.CallBackMethod;
import dev.knalis.sao_telegram_bot.callback.annotation.PathVariable;
import dev.knalis.sao_telegram_bot.composer.ComposerContext;
import dev.knalis.sao_telegram_bot.service.crud.GangMembershipService;
import dev.knalis.sao_telegram_bot.service.telegram.ConsumerService;
import dev.knalis.sao_telegram_bot.service.MenuService;
import dev.knalis.sao_telegram_bot.service.telegram.TelegramSenderService;
import dev.knalis.sao_telegram_bot.service.crud.impl.GangService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@CallBackController("gang")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GangCallBackController extends AbstractCallBackController {
    
    MenuService menuService;
    GangService gangService;
    ConsumerService consumerService;
    private final GangMembershipService gangMembershipService;
    
    public GangCallBackController(TelegramSenderService senderService, MenuService menuService, GangService gangService, ConsumerService consumerService, GangMembershipService gangMembershipService) {
        super(senderService);
        this.menuService = menuService;
        this.gangService = gangService;
        this.consumerService = consumerService;
        this.gangMembershipService = gangMembershipService;
    }

    @CallBackMethod
    public void openMenu(CallBackInfo info) {
        var userId = info.getUser().getId();
        var messageId = info.getMessageId();
        var context = new ComposerContext(userId);
        var message = menuService.getGangMenu(context);
        editMessage(userId, messageId, message);
    }
    
    @CallBackMethod("/kick/{targetId}")
    public void kickMember(@PathVariable("targetId") long targetId, CallBackInfo info) {
        var messageId = info.getMessageId();
        var userId = info.getUser().getId();
        safeExecute(userId, () -> {
            var gang = gangService.getGangByUserId(userId);
            var target = gang.getMembers().stream()
                    .filter(member -> member.getId() == targetId)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("User is not a member of the gang"));
            gangMembershipService.removeMember(gang, target, info.getUser());
            var context = new ComposerContext(userId);
            var message = menuService.getGangMenu(context);
            editMessage(userId, messageId, message);
        }, "❌ Не удалось исключить участника. Попробуйте позже.");
    }
    
    @CallBackMethod("/leave")
    public void leaveGang(CallBackInfo info) {
        var userId = info.getUser().getId();
        var messageId = info.getMessageId();
        safeExecute(userId, () -> {
            gangService.leaveGang(userId);
            var context = new ComposerContext(userId);
            var message = menuService.getAllGangMenu(context);
            editMessage(userId, messageId, message);
        }, "❌ Не удалось покинуть банду. Попробуйте позже.");
    }
    
    @CallBackMethod("/join/{gangId}")
    public void joinGang(@PathVariable("gangId") long gangId, CallBackInfo info) {
        var messageId = info.getMessageId();
        var userId = info.getUser().getId();
        safeExecute(userId, () -> {
            gangService.joinGang(userId, gangId);
            var context = new ComposerContext(userId);
            var message = menuService.getGangMenu(context);
            editMessage(userId, messageId, message);
        }, "❌ Не удалось вступить в банду. Попробуйте позже.");
    }
    
    @CallBackMethod("/create")
    public void createGang(CallBackInfo info) {
        var userId = info.getUser().getId();
        var messageId = info.getMessageId();
        var context = new ComposerContext(userId);
        int promptId = sendMessage(userId, "✏️ Введите название банды. Для отмены /cancel.");
        consumerService.addConsumer(userId, input -> {
            if (input.length() < 3 || input.length() > 20) {
                sendMessage(userId, "⚠️ Название банды должно быть от 3 до 20 символов.");
                return;
            }
            safeExecute(userId, () -> {
                gangService.createGang(userId, input);
                var message = menuService.getGangMenu(context);
                editMessage(userId, messageId, message);
                deleteMessage(userId, promptId);
            }, "❌ Не удалось создать банду. Попробуйте позже.");
        });
    }
    
    @CallBackMethod("/transfer/{newOwnerId}")
    public void transferOwnership(@PathVariable("newOwnerId") long newOwnerId, CallBackInfo info) {
        var messageId = info.getMessageId();
        var userId = info.getUser().getId();
        safeExecute(userId, () -> {
            gangService.transferOwnership(userId, newOwnerId);
            var context = new ComposerContext(userId);
            var message = menuService.getGangMenu(context);
            editMessage(userId, messageId, message);
        }, "❌ Не удалось передать права. Попробуйте позже.");
    }
    
}

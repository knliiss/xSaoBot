package dev.knalis.sao_telegram_bot.callback.impl;

import dev.knalis.sao_telegram_bot.callback.AbstractCallBackController;
import dev.knalis.sao_telegram_bot.callback.CallBackInfo;
import dev.knalis.sao_telegram_bot.callback.annotation.CallBackController;
import dev.knalis.sao_telegram_bot.callback.annotation.CallBackMethod;
import dev.knalis.sao_telegram_bot.callback.annotation.PathVariable;
import dev.knalis.sao_telegram_bot.composer.ComposerContext;
import dev.knalis.sao_telegram_bot.service.MenuService;
import dev.knalis.sao_telegram_bot.service.impl.GangServiceImpl;
import dev.knalis.sao_telegram_bot.service.intrf.GangService;
import dev.knalis.sao_telegram_bot.service.intrf.UserService;
import dev.knalis.sao_telegram_bot.service.telegram.ConsumerService;
import dev.knalis.sao_telegram_bot.service.telegram.TelegramSenderService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import dev.knalis.sao_telegram_bot.dto.entity.GangCreateDTO;
import dev.knalis.sao_telegram_bot.dto.entity.GangActionRequest;
import dev.knalis.sao_telegram_bot.dto.entity.GangActionType;

@CallBackController("gang")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GangCallBackController extends AbstractCallBackController {
    
    MenuService menuService;
    GangService gangService;
    ConsumerService consumerService;
    UserService userService;
    
    public GangCallBackController(TelegramSenderService senderService, MenuService menuService, GangService gangService, ConsumerService consumerService, UserService userService) {
        super(senderService);
        this.menuService = menuService;
        this.gangService = gangService;
        this.consumerService = consumerService;
        this.userService = userService;
    }
    
    @CallBackMethod("/kick/{targetId}")
    public void kickMember(@PathVariable("targetId") long targetId, CallBackInfo info) {
        var messageId = info.getMessageId();
        var userId = info.getUser().getId();
        safeExecute(userId, () -> {
            var gang = gangService.findAll().stream().filter(g -> g.getMembers().stream().anyMatch(m -> m.getId() == userId)).findFirst().orElseThrow(() -> new IllegalArgumentException("Gang not found for user"));
            GangActionRequest req = new GangActionRequest();
            req.setGangId(gang.getId());
            req.setActorId(userId);
            req.setTargetId(targetId);
            req.setActionType(GangActionType.KICK);
            gangService.executeAction(req);
            var context = new ComposerContext(userId);
            var message = menuService.getGangMenu(context);
            editMessage(userId, messageId, message);
        }, "❌ Не удалось исключить участника. Попробуйте позже.");
    }
    
    @CallBackMethod("/leave")
    public void leaveGang(CallBackInfo info) {
        var messageId = info.getMessageId();
        var userId = info.getUser().getId();
        safeExecute(userId, () -> {
            var gang = gangService.findAll().stream().filter(g -> g.getMembers().stream().anyMatch(m -> m.getId() == userId)).findFirst().orElseThrow(() -> new IllegalArgumentException("Gang not found for user"));
            GangActionRequest req = new GangActionRequest();
            req.setGangId(gang.getId());
            req.setActorId(userId);
            req.setTargetId(userId);
            req.setActionType(GangActionType.KICK);
            gangService.executeAction(req);
            var context = new ComposerContext(userId);
            var message = menuService.getGangMenu(context);
            editMessage(userId, messageId, message);
        }, "❌ Не удалось покинуть банду. Попробуйте позже.");
    }
    
    @CallBackMethod("/join/{gangId}")
    public void joinGang(@PathVariable("gangId") long gangId, CallBackInfo info) {
        sendMessage(info.getUser().getId(), "⚠️ Функция вступить в банду пока не поддерживается.");
    }
    
    @CallBackMethod("/create")
    public void createGang(CallBackInfo info) {
        var userDTO = info.getUser();
        var userId = userDTO.getId();
        var messageId = info.getMessageId();
        var context = new ComposerContext(userId);
        
        if (userDTO.getBalance() < GangServiceImpl.GANG_PRICE) {
            sendMessage(userId, "❌ У вас недостаточно средств для создания банды. Стоимость: " + GangServiceImpl.GANG_PRICE + " 💰.");
            return;
        }
        
        int promptId = sendMessage(userId, "✏️ Введите название банды. Для отмены /cancel.");
        consumerService.addConsumer(userId, input -> {
            if (input.length() < 3 || input.length() > 20) {
                sendMessage(userId, "⚠️ Название банды должно быть от 3 до 20 символов.");
                return;
            }
            safeExecute(userId, () -> {
                var user = userService.findById(info.getUser().getId()).orElseThrow();
                GangCreateDTO dto = GangCreateDTO.builder().name(input).createdBy(user).build();
                gangService.create(dto);
                var message = menuService.getGangMenu(context);
                editMessage(userId, messageId, message);
                deleteMessage(userId, promptId);
            }, "❌ Не удалось создать банду. Попробуйте позже.");
        });
    }
    
    @CallBackMethod("/transfer/{newOwnerId}")
    public void transferOwnership(@PathVariable("newOwnerId") long newOwnerId, CallBackInfo info) {
        sendMessage(info.getUser().getId(), "⚠️ Передача прав пока не поддерживается.");
    }
    
}

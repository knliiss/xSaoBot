package dev.knalis.sao_telegram_bot.callback.impl;

import dev.knalis.sao_telegram_bot.callback.AbstractCallBackController;
import dev.knalis.sao_telegram_bot.callback.CallBackInfo;
import dev.knalis.sao_telegram_bot.callback.annotation.CallBackController;
import dev.knalis.sao_telegram_bot.callback.annotation.CallBackMethod;
import dev.knalis.sao_telegram_bot.callback.annotation.PathVariable;
import dev.knalis.sao_telegram_bot.dto.entity.GangActionRequest;
import dev.knalis.sao_telegram_bot.dto.entity.GangActionType;
import dev.knalis.sao_telegram_bot.dto.entity.GangCreateDTO;
import dev.knalis.sao_telegram_bot.service.impl.GangServiceImpl;
import dev.knalis.sao_telegram_bot.service.intrf.GangService;
import dev.knalis.sao_telegram_bot.service.intrf.UserService;
import dev.knalis.sao_telegram_bot.service.telegram.ConsumerService;
import dev.knalis.sao_telegram_bot.service.telegram.TelegramSenderService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@CallBackController("gang")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GangCallBackController extends AbstractCallBackController {
    
    GangService gangService;
    ConsumerService consumerService;
    UserService userService;
    MenuCallBackController menuCallBackController;
    
    public GangCallBackController(TelegramSenderService senderService, GangService gangService, ConsumerService consumerService, UserService userService, MenuCallBackController menuCallBackController) {
        super(senderService);
        this.gangService = gangService;
        this.consumerService = consumerService;
        this.userService = userService;
        this.menuCallBackController = menuCallBackController;
    }
    
    @CallBackMethod("/kick/{targetId}")
    public void kickMember(@PathVariable("targetId") long targetId, CallBackInfo info) {
        var userId = info.getUser().getId();
        safeExecute(userId, () -> {
            var gang = gangService.findAll().stream().filter(g -> g.getMembers().stream().anyMatch(m -> m.getId() == userId)).findFirst().orElseThrow(() -> new IllegalArgumentException("Gang not found for user"));
            GangActionRequest req = new GangActionRequest();
            req.setGangId(gang.getId());
            req.setActorId(userId);
            req.setTargetId(targetId);
            req.setActionType(GangActionType.KICK);
            gangService.executeAction(req);
            menuCallBackController.gangMenu(userId, info);
        }, "❌ Не удалось исключить участника. Попробуйте позже.");
    }
    
    @CallBackMethod("/leave")
    public void leaveGang(CallBackInfo info) {
        var userId = info.getUser().getId();
        safeExecute(userId, () -> {
            var gang = gangService.findAll().stream().filter(g -> g.getMembers().stream().anyMatch(m -> m.getId() == userId)).findFirst().orElseThrow(() -> new IllegalArgumentException("Gang not found for user"));
            
            GangActionRequest req = new GangActionRequest();
            req.setGangId(gang.getId());
            req.setActorId(userId);
            req.setTargetId(userId);
            req.setActionType(GangActionType.KICK);
            gangService.executeAction(req);
            
            menuCallBackController.gangMenu(userId, info);
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
                
                menuCallBackController.gangMenu(userId, info);
                deleteMessage(userId, promptId);
            }, "❌ Не удалось создать банду. Попробуйте позже.");
        });
    }
    
    @CallBackMethod("/transfer/{newOwnerId}")
    public void transferOwnership(@PathVariable("newOwnerId") long newOwnerId, CallBackInfo info) {
        sendMessage(info.getUser().getId(), "⚠️ Передача прав пока не поддерживается.");
    }
    
}

package dev.knalis.sao_telegram_bot.callback.impl;

import dev.knalis.sao_telegram_bot.callback.AbstractCallBackController;
import dev.knalis.sao_telegram_bot.callback.CallBackInfo;
import dev.knalis.sao_telegram_bot.callback.annotation.CallBackController;
import dev.knalis.sao_telegram_bot.callback.annotation.CallBackMethod;
import dev.knalis.sao_telegram_bot.callback.annotation.PathVariable;
import dev.knalis.sao_telegram_bot.dto.entity.IdeaCreateRequest;
import dev.knalis.sao_telegram_bot.dto.entity.ReactionRequest;
import dev.knalis.sao_telegram_bot.model.IdeaStatus;
import dev.knalis.sao_telegram_bot.model.ReactionType;
import dev.knalis.sao_telegram_bot.model.user.Role;
import dev.knalis.sao_telegram_bot.service.intrf.IdeaReactionService;
import dev.knalis.sao_telegram_bot.service.intrf.IdeaService;
import dev.knalis.sao_telegram_bot.service.telegram.ConsumerService;
import dev.knalis.sao_telegram_bot.service.telegram.TelegramSenderService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@CallBackController("idea")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IdeaCallBackController extends AbstractCallBackController {
    
    IdeaService ideaService;
    IdeaReactionService ideaReactionService;
    ConsumerService consumerService;
    MenuCallBackController menuCallBackController;
    
    public IdeaCallBackController(TelegramSenderService senderService, IdeaService ideaService, IdeaReactionService ideaReactionService, ConsumerService consumerService, MenuCallBackController menuCallBackController) {
        super(senderService);
        this.ideaService = ideaService;
        this.ideaReactionService = ideaReactionService;
        this.consumerService = consumerService;
        this.menuCallBackController = menuCallBackController;
    }
    
    @CallBackMethod("/create")
    public void create(CallBackInfo info) {
        var chatId = info.getUser().getId();
        safeExecute(chatId, () -> {
            var ideaCreateRequest = new IdeaCreateRequest();
            var promptMessageId = sendMessage(chatId, "📝 Пожалуйста, введите краткое название идеи:");
            consumerService.addConsumer(chatId, message -> {
                ideaCreateRequest.setTitle(message);
                var descriptionMessageId = sendMessage(chatId, "📝 Теперь введите подробное описание вашей идеи:");
                consumerService.addConsumer(chatId, descMessage -> {
                    ideaCreateRequest.setContent(descMessage);
                    ideaCreateRequest.setAuthorId(chatId);
                    ideaService.create(ideaCreateRequest);
                    sendMessage(chatId, "✅ Ваша идея успешно создана и отправлена на рассмотрение!");
                    deleteMessage(chatId, promptMessageId);
                    deleteMessage(chatId, descriptionMessageId);
                });
            });

        }, "❌ Не удалось открыть меню создания идеи. Попробуйте позже.");
    }
    
    @CallBackMethod("/delete/{ideaId}/{page}")
    public void delete(@PathVariable("ideaId") long ideaId, @PathVariable("page") int page, CallBackInfo info) {
        var chatId = info.getUser().getId();
        
        safeExecute(chatId, () -> {
            if (!ideaService.canDelete(ideaId, chatId)) {
                sendMessage(chatId, "❌ У вас нет прав для выполнения этого действия.");
                return;
            }
            ideaService.delete(ideaId);
            menuCallBackController.ideaMenu(chatId, page, info);
        }, "❌ Не удалось удалить идею. Попробуйте позже.");
    }
    
    @CallBackMethod("/react/{ideaId}/{reactionType}")
    public void react(@PathVariable("ideaId") long ideaId, @PathVariable("reactionType") String reactionType, CallBackInfo info) {
        var chatId = info.getUser().getId();
        safeExecute(chatId, () -> {
            long reactionId = ideaReactionService.findByIdeaIdAndUserId(ideaId, chatId)
                    .map(r -> r.getId())
                    .orElse(-1L);
            if (reactionType.equals("remove")) {
                ideaReactionService.delete(reactionId);
            } else {
                var reactionReq = new ReactionRequest();
                reactionReq.setIdeaId(ideaId);
                reactionReq.setUserId(chatId);
                reactionReq.setReactionType(ReactionType.valueOf(reactionType));
                ideaReactionService.save(reactionReq);
            }
            var message = "✅ Ваша реакция учтена!\n\n" +
                    "Перезапустите просмотр идеи, чтобы увидеть обновленные реакции.";
            sendMessage(chatId, message);
        }, "❌ Не удалось поставить реакцию. Попробуйте позже.");
    }
    
    @CallBackMethod("/moderate/{ideaId}/{action}")
    public void moderate(@PathVariable("ideaId") long ideaId, @PathVariable("action") String action, CallBackInfo info) {
        var chatId = info.getUser().getId();
        var userDTO = info.getUser();
        
        if (!userDTO.getRoles().contains(Role.ADMIN)) {
            sendMessage(chatId, "❌ У вас нет прав для выполнения этого действия.");
            return;
        }
        
        safeExecute(chatId, () -> {
            ideaService.updateStatus(ideaId, IdeaStatus.valueOf(action.toUpperCase()));
            var message = "✅ Действие выполнено!\n\n" +
                    "Перезапустите просмотр идеи, чтобы увидеть обновленный статус.";
            sendMessage(chatId, message);
        }, "❌ Не удалось выполнить модерацию. Попробуйте позже.");
    }
}

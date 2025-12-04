package dev.knalis.sao_telegram_bot.service.telegram;

import dev.knalis.sao_telegram_bot.dto.MessageRequest;
import io.github.bucket4j.Bucket;
import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class MessageWorker {

    private final MessageQueueService queueService;
    private final TelegramSenderService senderService;
    private final Bucket rateLimiter;

    public MessageWorker(MessageQueueService queueService,
                         TelegramSenderService senderService,
                         Bucket rateLimiter) {
        this.queueService = queueService;
        this.senderService = senderService;
        this.rateLimiter = rateLimiter;
    }

    @PostConstruct
    @Async
    public void startWorker() {
        while (true) {
            MessageRequest request = queueService.dequeue();
            if (request != null && rateLimiter.tryConsume(1)) {
                senderService.sendMessage(request.getChatId(), request.getText());
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
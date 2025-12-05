package dev.knalis.sao_telegram_bot.service.telegram;

import dev.knalis.sao_telegram_bot.dto.MessageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class MessageQueueService {

    private static final String QUEUE_KEY = "telegram:queue";
    private final Queue<MessageRequest> queue = new ConcurrentLinkedQueue<>();

    public MessageQueueService() {
    }

    public void enqueue(MessageRequest message) {
        queue.offer(message);
    }

    @Async
    public void enqueueBulk(Iterable<MessageRequest> messages) {
        messages.forEach(this::enqueue);
    }

    public MessageRequest dequeue() {
        return queue.poll();
    }

    public long getQueueSize() {
        return queue.size();
    }
}
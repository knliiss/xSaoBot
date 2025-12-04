package dev.knalis.sao_telegram_bot.service.telegram;

import dev.knalis.sao_telegram_bot.dto.MessageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MessageQueueService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String QUEUE_KEY = "telegram:queue";

    public MessageQueueService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void enqueue(MessageRequest message) {
        redisTemplate.opsForList().rightPush(QUEUE_KEY, message);
    }

    @Async
    public void enqueueBulk(Iterable<MessageRequest> messages) {
        messages.forEach(this::enqueue);
    }

    public MessageRequest dequeue() {
        Object obj = redisTemplate.opsForList().leftPop(QUEUE_KEY);
        return obj instanceof MessageRequest ? (MessageRequest) obj : null;
    }

    public long getQueueSize() {
        Long size = redisTemplate.opsForList().size(QUEUE_KEY);
        return size != null ? size : 0;
    }
}
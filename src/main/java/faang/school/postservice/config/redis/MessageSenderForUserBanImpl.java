package faang.school.postservice.config.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MessageSenderForUserBanImpl implements MessageSender {

    private final RedisTemplate<String, List<Long>> redisTemplate;
    private final ChannelTopic channelTopic;

    @Override
    public void send(String message) {
        redisTemplate.convertAndSend(channelTopic.getTopic(), message);
    }
}
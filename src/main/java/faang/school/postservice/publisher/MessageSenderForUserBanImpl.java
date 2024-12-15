package faang.school.postservice.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageSenderForUserBanImpl implements MessageSender {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic channelTopicForUserBan;

    @Override
    public void send(String message) {
        redisTemplate.convertAndSend(channelTopicForUserBan.getTopic(), message);
    }
}
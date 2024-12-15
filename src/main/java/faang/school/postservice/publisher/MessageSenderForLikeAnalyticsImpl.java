package faang.school.postservice.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageSenderForLikeAnalyticsImpl implements MessageSender {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic channelTopicForLikeAnalytics;

    @Override
    public void send(String message) {
        redisTemplate.convertAndSend(channelTopicForLikeAnalytics.getTopic(), message);
        log.info("Published message: {}", message);
    }
}

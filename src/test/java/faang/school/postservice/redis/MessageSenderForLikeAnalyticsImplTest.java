package faang.school.postservice.redis;

import faang.school.postservice.config.redis.MessageSenderForLikeAnalyticsImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class MessageSenderForLikeAnalyticsImplTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ChannelTopic channelTopicForLikeAnalytics;
    @InjectMocks
    private MessageSenderForLikeAnalyticsImpl messageSender;

    @Test
    @DisplayName("Test send - Successful Message Publishing")
    public void testSend_Success() {
        String message = "test message";
        when(channelTopicForLikeAnalytics.getTopic()).thenReturn("like-analytics-topic");
        messageSender.send(message);
        verify(redisTemplate, times(1)).convertAndSend("like-analytics-topic", message);
        verify(channelTopicForLikeAnalytics, times(1)).getTopic();
        verifyNoMoreInteractions(redisTemplate, channelTopicForLikeAnalytics);
    }
}

package faang.school.postservice.config.redis;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class RedisProperties {
    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.port}")
    private Integer port;
    @Value("${spring.data.redis.user-ban-topic}")
    private String userBanTopic;
    @Value("${spring.data.redis.like-analytics-topic}")
    private String likeAnalyticsTopic;
}
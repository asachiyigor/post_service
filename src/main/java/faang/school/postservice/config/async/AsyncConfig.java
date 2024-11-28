package faang.school.postservice.config.async;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Data
@Configuration
public class AsyncConfig {
    @Value("${spring.async.pool-size}")
    private int poolSize;
    @Value("${spring.async.max-pool-size}")
    private int maxPoolSize;
    @Value("${spring.async.queue-capacity}")
    private int queueCapacity;
    @Value("${spring.async.thread-name-prefix}")
    private String threadNamePrefix;

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(poolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();
        return executor;
    }
}

package faang.school.postservice.config.async;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorConfig {
    @Value("${spring.scheduler.post.publisher.threads-count}")
    private int threadsCountForPostPublisher;

    @Bean(name="executorPostPublisher")
    public ExecutorService executorPostPublisher() {
        return Executors.newFixedThreadPool(threadsCountForPostPublisher);
    }
}

package faang.school.postservice.scheduler.post;


import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class ScheduledPostPublisher {
    private final PostService postService;

    @Value("${spring.scheduler.post.publisher.partition-size}")
    private Integer partitionSize;

    @Scheduled(cron = "${spring.scheduler.post.publisher.cron}")
    public void publishPosts() {
        postService.publishScheduledPosts(partitionSize);
    }
}

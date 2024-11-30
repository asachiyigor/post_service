package faang.school.postservice.sheduler.authorbanner;

import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthorBanner {
    private final PostService postService;

    @Scheduled(cron = "${auto-checking-posts-by-verification.cron}")
    @Retryable(retryFor = {IOException.class}, maxAttempts = 5, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void authorBanner() throws IOException {
        postService.checkPostsForVerification();
    }
}

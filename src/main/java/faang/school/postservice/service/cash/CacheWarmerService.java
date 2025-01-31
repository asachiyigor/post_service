package faang.school.postservice.service.cash;

import com.google.common.collect.Lists;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostVisibility;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.redis.entities.CommentCache;
import faang.school.postservice.redis.entities.PostCache;
import faang.school.postservice.redis.entities.UserCache;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.PostCacheRepository;
import faang.school.postservice.repository.redis.UserCacheRepository;
import faang.school.postservice.service.feed.UserFeedZSetService;
import faang.school.postservice.service.subscription.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CacheWarmerService {
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final PostCacheRepository postCacheRepository;
    private final UserCacheRepository userCacheRepository;
    private final UserFeedZSetService userFeedZSetService;
    private final UserContext userContext;
    private final SubscriptionService subscriptionService;
    private final CommentCacheService commentCacheService;

    @Value("${spring.data.cache.warmup.batch-size}")
    private int batchSize;

    @Async("taskExecutor")
    public void warmUpCache() {
        log.info("Starting cache warm-up process");
        try {
            ExecutorService executor = createExecutor();
            try {
                long currentUserId = userContext.getUserId();
                Set<Long> activeAuthors = subscriptionService.getAuthorIds();
                Set<Long> activeProjects = subscriptionService.getProjectIds();

                CompletableFuture<Void> usersCaching = CompletableFuture.runAsync(() -> {
                    userContext.setUserId(currentUserId);
                    try {
                        warmUpUsers(activeAuthors);
                    } finally {
                        userContext.clear();
                    }
                }, executor);

                CompletableFuture<Void> postsCaching = CompletableFuture.runAsync(() -> {
                    userContext.setUserId(currentUserId);
                    try {
                        warmUpPosts(activeAuthors);
                    } finally {
                        userContext.clear();
                    }
                }, executor);

                CompletableFuture<Void> feedsCaching = CompletableFuture.runAsync(() -> {
                    userContext.setUserId(currentUserId);
                    try {
                        warmUpFeeds(activeAuthors, activeProjects);
                    } finally {
                        userContext.clear();
                    }
                }, executor);

                CompletableFuture.allOf(usersCaching, postsCaching, feedsCaching).get();
                log.info("Cache warm-up completed successfully");
            } finally {
                executor.shutdown();
            }
        } catch (Exception e) {
            log.error("Error during cache warm-up", e);
            throw new RuntimeException("Cache warm-up failed", e);
        }
    }

    private ExecutorService createExecutor() {
        return Executors.newFixedThreadPool(3, r -> {
            Thread thread = new Thread(r);
            thread.setContextClassLoader(this.getClass().getClassLoader());
            userContext.getUserId();
            thread.setUncaughtExceptionHandler((t, e) ->
                    log.error("Uncaught exception in thread: {}", t.getName(), e));
            return thread;
        });
    }

    private void warmUpUsers(Set<Long> userIds) {
        try {
            int totalUsers = userIds.size();
            int processedUsers = 0;

            for (List<Long> batch : Lists.partition(new ArrayList<>(userIds), batchSize)) {
                for (Long userId : batch) {
                    try {
                        userServiceClient.findById(userId).ifPresent(user -> {
                            UserCache userCache = createUserCache(user);
                            userCacheRepository.save(userCache);
                        });
                    } catch (Exception e) {
                        log.error("Error caching user {}", userId, e);
                    }
                }
                processedUsers += batch.size();
                log.info("Cached {} users out of {}", processedUsers, totalUsers);
            }
        } catch (Exception e) {
            log.error("Error in warmUpUsers", e);
            throw e;
        }
    }

    private void warmUpPosts(Set<Long> authorIds) {
        try {
            AtomicInteger totalProcessed = new AtomicInteger(0);

            for (Long userId : authorIds) {
                try {
                    List<Post> posts = postRepository.findByPublishedAndNotDeletedAndAuthorIdOrderCreatedAtDesc(userId);
                    for (Post post : posts) {
                        if (post.canBeAddedToFeed()) {
                            try {
                                PostCache postCache = createPostCache(post);
                                postCache.setLastComments(commentCacheService.fetchLatestComments(post.getId()));
                                postCacheRepository.save(postCache);
                                totalProcessed.incrementAndGet();
                            } catch (Exception e) {
                                log.error("Error caching post {} for user {}", post.getId(), userId, e);
                            }
                        }
                    }
                    log.info("Processed {} posts for user {}, total posts processed: {}",
                            posts.size(), userId, totalProcessed.get());
                } catch (Exception e) {
                    log.error("Error processing posts for user {}", userId, e);
                }
            }
            log.info("Finished caching posts. Total posts processed: {}", totalProcessed.get());
        } catch (Exception e) {
            log.error("Error in warmUpPosts", e);
            throw e;
        }
    }

    private void warmUpFeeds(Set<Long> activeAuthorIds, Set<Long> projectIds) {
        try {
            AtomicInteger totalProcessedSubscribers = new AtomicInteger(0);
            processUserFeeds(activeAuthorIds, totalProcessedSubscribers);
            processProjectFeeds(projectIds, totalProcessedSubscribers);

            log.info("Completed all feed warm-ups. Total subscribers processed: {}", totalProcessedSubscribers.get());
        } catch (Exception e) {
            log.error("Error during feed warm-up process", e);
            throw e;
        }
    }

    private void processUserFeeds(Set<Long> activeAuthorIds, AtomicInteger totalProcessed) {
        for (Long authorId : activeAuthorIds) {
            try {
                List<Post> authorPosts = postRepository.findByPublishedAndNotDeletedAndAuthorIdOrderCreatedAtDesc(authorId);
                if (authorPosts.isEmpty()) {
                    continue;
                }

                List<Post> validPosts = authorPosts.stream()
                        .filter(Post::canBeAddedToFeed)
                        .collect(Collectors.toList());

                if (validPosts.isEmpty()) {
                    continue;
                }

                List<Long> subscriberIds = userServiceClient.getUserSubscribersIds(authorId);
                log.info("Found {} subscribers for author {}", subscriberIds.size(), authorId);

                updateSubscriberFeeds(subscriberIds, validPosts, totalProcessed, "author", authorId);
            } catch (Exception e) {
                log.error("Failed to process author {} feeds", authorId, e);
            }
        }
    }

    private void processProjectFeeds(Set<Long> projectIds, AtomicInteger totalProcessed) {
        for (Long projectId : projectIds) {
            try {
                List<Post> projectPosts = postRepository.findByPublishedAndNotDeletedAndProjectIdOrderCreatedAtDesc(projectId);
                if (projectPosts.isEmpty()) {
                    continue;
                }

                List<Post> validPosts = projectPosts.stream()
                        .filter(Post::canBeAddedToFeed)
                        .collect(Collectors.toList());

                if (validPosts.isEmpty()) {
                    continue;
                }

                List<Long> subscriberIds = userServiceClient.getProjectSubscriptions(projectId);
                log.info("Found {} subscribers for project {}", subscriberIds.size(), projectId);

                updateSubscriberFeeds(subscriberIds, validPosts, totalProcessed, "project", projectId);
            } catch (Exception e) {
                log.error("Failed to process project {} feeds", projectId, e);
            }
        }
    }

    private void updateSubscriberFeeds(
            List<Long> subscriberIds,
            List<Post> posts,
            AtomicInteger totalProcessed,
            String sourceType,
            Long sourceId) {

        for (List<Long> batch : Lists.partition(subscriberIds, batchSize)) {
            for (Long subscriberId : batch) {
                try {
                    for (Post post : posts) {
                        userFeedZSetService.addPostToFeed(
                                subscriberId,
                                post.getId(),
                                post.getCreatedAt()
                        );
                    }
                    totalProcessed.incrementAndGet();
                } catch (Exception e) {
                    log.error("Failed to update feed for subscriber {} of {} {}",
                            subscriberId, sourceType, sourceId, e);
                }
            }
            log.info("Processed batch of {} subscribers for {} {}, total processed: {}",
                    batch.size(), sourceType, sourceId, totalProcessed.get());
        }
    }

    private UserCache createUserCache(UserDto user) {
        return UserCache.builder()
                .id(user.getId())
                .username(user.getUsername())
                .build();
    }

    private PostCache createPostCache(Post post) {
        return PostCache.builder()
                .id(post.getId())
                .authorId(post.getAuthorId())
                .projectId(post.getProjectId())
                .content(post.getContent())
                .updatedAt(post.getUpdatedAt())
                .publishedAt(post.getPublishedAt())
                .verified(post.isVerified())
                .visibility(post.isVisible() ? PostVisibility.PUBLIC : PostVisibility.PRIVATE)
                .likesCount(post.getLikesCount())
                .commentsCount(post.getCommentsCount())
                .build();
    }

    private CommentCache createCommentCache(Comment comment) {
        return CommentCache.builder()
                .id(comment.getId())
                .authorId(comment.getAuthorId())
                .content(comment.getContent())
                .postId(comment.getPost().getId())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
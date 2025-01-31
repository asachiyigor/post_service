package faang.school.postservice.service.feed;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.feed.AuthorDTO;
import faang.school.postservice.dto.feed.FeedResponse;
import faang.school.postservice.dto.feed.PostDTO;
import faang.school.postservice.dto.post.PostVisibility;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.redis.entities.CommentCache;
import faang.school.postservice.redis.entities.PostCache;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.PostCacheRepository;
import faang.school.postservice.service.cash.CommentCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {
    private static final int MAX_PAGE_SIZE = 20;
    private static final int FEED_SIZE = 500;

    private final PostCacheRepository postCacheRepository;
    private final PostRepository postRepository;
    private final CommentCacheService commentCacheService;
    private final UserFeedZSetService userFeedZSetService;

    public FeedResponse getFeed(Long userId, Long lastPostId, int pageSize) {
        pageSize = validateAndAdjustPageSize(pageSize);
        List<Long> postIds = getPostIdsForFeed(userId, lastPostId, pageSize);
        List<PostDTO> posts = fetchAndMapPosts(postIds);
        return buildFeedResponse(posts, pageSize);
    }

    private int validateAndAdjustPageSize(int pageSize) {
        return Math.min(pageSize, MAX_PAGE_SIZE);
    }

    private List<Long> getPostIdsForFeed(Long userId, Long lastPostId, int pageSize) {
        List<Long> postIds = userFeedZSetService.getFeedPosts(userId, lastPostId, pageSize);
        if (postIds.isEmpty()) {
            loadUserFeedFromDatabase(userId);
            postIds = userFeedZSetService.getFeedPosts(userId, lastPostId, pageSize);
        }
        return postIds;
    }

    private void loadUserFeedFromDatabase(Long userId) {
        postRepository.findLatestPostsForUser(userId, PageRequest.of(0, FEED_SIZE))
                .stream()
                .filter(Post::canBeAddedToFeed)
                .forEach(post -> userFeedZSetService.addPostToFeed(
                        userId, post.getId(), post.getCreatedAt()));
    }

    private List<PostDTO> fetchAndMapPosts(List<Long> postIds) {
        return postIds.stream()
                .map(this::getPost)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    private Optional<PostDTO> getPost(Long postId) {
        return getCachedPost(postId)
                .or(() -> getDatabasePost(postId));
    }

    private Optional<PostDTO> getCachedPost(Long postId) {
        return postCacheRepository.findById(postId)
                .flatMap(post -> {
                    Optional<Post> dbPostOpt = postRepository.findById(postId)
                            .filter(Post::canBeAddedToFeed);

                    return dbPostOpt.isPresent() ? Optional.of(mapToDTO(post)) : Optional.empty();
                });
    }

    private Optional<PostDTO> getDatabasePost(Long postId) {
        return postRepository.findById(postId)
                .filter(Post::canBeAddedToFeed)
                .map(post -> mapToDTO(cachePost(post)));
    }

    private PostCache cachePost(Post post) {
        PostCache postCache = createPostCache(post);
        postCache.setLastComments(commentCacheService.fetchLatestComments(post.getId()));
        return postCacheRepository.save(postCache);
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

    private PostDTO mapToDTO(PostCache post) {
        AuthorDTO author = null;
        ProjectDto project = null;

        if (post.getAuthorId() != null) {
            author = AuthorDTO.builder()
                    .id(post.getAuthorId())
                    .build();
        } else if (post.getProjectId() != null) {
            project = ProjectDto.builder()
                    .id(post.getProjectId())
                    .build();
        }

        return PostDTO.builder()
                .id(post.getId())
                .content(post.getContent())
                .author(author)
                .project(project)
                .updatedAt(post.getUpdatedAt())
                .publishedAt(post.getPublishedAt())
                .verified(post.isVerified())
                .visibility(post.getVisibility())
                .likesCount(post.getLikesCount())
                .commentsCount(post.getCommentsCount())
                .lastComments(mapComments(post.getLastComments()))
                .build();
    }

    private List<CommentDto> mapComments(LinkedHashSet<CommentCache> comments) {
        if (comments == null) {
            return Collections.emptyList();
        }
        return comments.stream()
                .map(this::mapCommentToDTO)
                .collect(Collectors.toList());
    }

    private CommentDto mapCommentToDTO(CommentCache comment) {
        if (comment == null) {
            return null;
        }
        return CommentDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .authorId(comment.getAuthorId())
                .postId(comment.getPostId())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    private FeedResponse buildFeedResponse(List<PostDTO> posts, int pageSize) {
        return FeedResponse.builder()
                .posts(posts)
                .hasMore(posts.size() >= pageSize)
                .lastPostId(posts.isEmpty() ? null : posts.get(posts.size() - 1).getId())
                .build();
    }
}
package faang.school.postservice.service.like;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.redis.MessageSenderForLikeAnalyticsImpl;
import faang.school.postservice.dto.like.AnalyticsEventDto;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.like.LikeDtoForComment;
import faang.school.postservice.dto.like.LikeDtoForPost;
import faang.school.postservice.dto.like.ResponseLikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validator.dto.user.UserDtoValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final LikeMapper likeMapper;
    private final UserServiceClient userServiceClient;
    private final PostService postService;
    private final CommentService commentService;
    private final UserDtoValidator userDtoValidator;
    private final MessageSenderForLikeAnalyticsImpl likeEventPublisher;
    private final ObjectMapper objectMapper;

    @Transactional
    public ResponseLikeDto addLikeByPost(LikeDtoForPost likeDtoForPost) throws IOException {
        userDtoExists(likeDtoForPost);
        Post post = postService.findPostById(likeDtoForPost.getPostId());

        likeRepository.findByPostIdAndUserId(likeDtoForPost.getPostId(),
                likeDtoForPost.getUserId()).ifPresent(like -> {
            throw new IllegalArgumentException("User has already liked this post");
        });

        Like likeForPost = Like
                .builder()
                .userId(likeDtoForPost.getUserId())
                .post(post)
                .build();
        likeRepository.save(likeForPost);

        publishLikeEvent(likeDtoForPost);

        return likeMapper.toLikeDtoFromEntity(likeForPost);
    }

    private void publishLikeEvent(LikeDtoForPost likeDtoForPost) throws IOException {
        AnalyticsEventDto likeAnalyticsDto = AnalyticsEventDto
                .builder()
                .actorId(likeDtoForPost.getUserId())
                .receiverId(postService.findPostById(likeDtoForPost.getPostId()).getAuthorId())
                .receivedAt(LocalDateTime.now())
                .build();
        likeEventPublisher.send(objectMapper.writeValueAsString(likeAnalyticsDto));
    }

    @Transactional
    public void deleteLikeFromPost(LikeDtoForPost likeDtoForPost) {
        if (!postService.existsPost(likeDtoForPost.getPostId())) {
            throw new EntityNotFoundException("Post not found");
        }
        likeRepository.deleteByPostIdAndUserId(likeDtoForPost.getPostId(),
                likeDtoForPost.getUserId());
    }

    @Transactional
    public ResponseLikeDto addLikeByComment(LikeDtoForComment likeDtoForComment) {
        userDtoExists(likeDtoForComment);
        Comment comment = commentService.findCommentById(likeDtoForComment.getCommentId());

        likeRepository.findByCommentIdAndUserId(likeDtoForComment.getCommentId(), likeDtoForComment.getUserId())
                .ifPresent(like -> {
                    throw new IllegalArgumentException("User has already liked this comment");
                });

        Like likeForComment = Like
                .builder()
                .userId(likeDtoForComment.getUserId())
                .comment(comment)
                .build();

        likeRepository.save(likeForComment);
        return likeMapper.toLikeDtoFromEntity(likeForComment);
    }

    @Transactional
    public void deleteLikeFromComment(LikeDtoForComment likeDtoForComment) {
        if (!commentService.isExits(likeDtoForComment.getCommentId())) {
            throw new IllegalArgumentException("Comment not found");
        }
        likeRepository.deleteByCommentIdAndUserId(likeDtoForComment.getCommentId(), likeDtoForComment.getUserId());
    }

    private <T extends LikeDto> void userDtoExists(T verifiedDto) {
        long userId = verifiedDto.getUserId();
        UserDto userDto = userServiceClient.getUser(userId);
        userDtoValidator.validateUserDto(userDto);
    }
}

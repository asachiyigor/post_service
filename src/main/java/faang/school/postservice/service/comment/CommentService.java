package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.ResponseCommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;

import faang.school.postservice.validator.CommentIdValidator;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.comment.CommentValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserServiceClient userServiceClient;
    private final CommentIdValidator commentIdValidator;
    private final CommentValidator commentValidator;
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;
    private final UserContext userContext;

    public ResponseCommentDto addComment(Long postId, CommentDto commentDto) {
        validateUser(commentDto.getAuthorId());
        Comment comment = commentMapper.toEntity(commentDto);
        comment.setAuthorId(userContext.getUserId());
        comment.setPost(getPost(postId));
        comment = commentRepository.save(comment);
        return commentMapper.toResponseDto(comment);
    }

    public ResponseCommentDto updateComment(CommentDto receivedCommentDto) {
        Comment actualComment = getComment(receivedCommentDto.getId());
        commentValidator.validComment(actualComment, receivedCommentDto);
        actualComment.setContent(receivedCommentDto.getContent());
        actualComment = commentRepository.save(actualComment);
        return commentMapper.toResponseDto(actualComment);
    }

    public List<ResponseCommentDto> getCommentsByPostId(Long postId) {
        Post post = getPost(postId);
        commentValidator.validPostComments(post);
        List<Comment> comments = post.getComments();
        return comments.stream()
                .sorted((comment1, comment2) -> comment1.getCreatedAt().compareTo(comment2.getCreatedAt()))
                .map(commentMapper::toResponseDto)
                .toList();
    }

    public void deleteComment(Long commentId) {
        existsComment(commentId);
        commentRepository.deleteById(commentId);
    }

    private void validateUser(Long userId) {
        UserDto userDto = userServiceClient.getUser(userId);
        if (userDto == null) {
            throw new EntityNotFoundException(String.format("Юзера с id %d не существует!", userId));
        }
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(() ->
                new EntityNotFoundException("Такого поста не существует"));
    }

    private Comment getComment(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() ->
                new EntityNotFoundException("Комментарий не найден"));
    }

    private void existsComment(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new EntityNotFoundException("Такого комментария не существует");
        }
    }

    public Comment findCommentById(Long commentId) {
        commentIdValidator.validateCommentId(commentId);
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
    }

    public boolean isExits(Long commentId) {
        commentIdValidator.validateCommentId(commentId);
        return commentRepository.existsById(commentId);
    }
}

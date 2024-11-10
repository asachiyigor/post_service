package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.ResponseCommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.PostException;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.comment.CommentValidator;
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
    private final CommentValidator commentValidator;
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;
    private final UserContext userContext;

    public ResponseCommentDto addComment(Long postId, CommentDto commentDto) {
        validateUser(commentDto.getAuthorId());
        Comment comment = commentMapper.toEntity(commentDto);
        comment.setPost(getPost(postId));
        comment.setAuthorId(userContext.getUserId());
        comment = commentRepository.save(comment);
        return commentMapper.toResponseDto(comment);
    }

    public ResponseCommentDto updateComment(CommentDto receivedCommentDto) {
        Comment actualComment = getComment(receivedCommentDto.getId());
        commentValidator.valideComment(actualComment, receivedCommentDto);
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
        commentRepository.deleteById(commentId);
    }

    private void validateUser(Long userId) {
        UserDto userDto = userServiceClient.getUser(userId);
        if (userDto != null) {
            throw new PostException(String.format("Юзера с id %d не существует!", userId));
        }
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new PostException("Такого поста не существует"));
    }

    private Comment getComment(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> new PostException("Комментарий не найден"));
    }
}

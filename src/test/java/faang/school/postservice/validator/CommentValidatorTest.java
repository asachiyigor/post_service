package faang.school.postservice.validator;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exception.PostException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.validator.comment.CommentValidator;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class CommentValidatorTest {

    @Spy
    private CommentValidator commentValidator;

    @ParameterizedTest
    @ValueSource(strings = {})
    void testValidComment(Comment actualComment, CommentDto expectedCommentDto) {
        assertThrows(PostException.class, () -> commentValidator.validComment(actualComment, expectedCommentDto));
    }

    private List<Comment> getComments() {
        return List.of();
    }

    private List
}

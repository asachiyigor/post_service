package faang.school.postservice.validator;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class CommentValidatorTest {

    @ParameterizedTest
    @ValueSource(strings = {})
    void testValidComment(Comment actualComment, CommentDto expectedCommentDto) {

    }

    private List<Comment> getComments() {
        return List.of();
    }
}

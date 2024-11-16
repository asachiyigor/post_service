package faang.school.postservice.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class CommentIdValidatorTest {

    @InjectMocks
    private CommentIdValidator commentIdValidator;

    @Test
    public void positiveValidateCommentId() {
        Long commentId = 1L;
        assertDoesNotThrow(() ->
                commentIdValidator.validateCommentId(commentId));
    }

    @Test
    public void negativeValidateCommentId() {
        Long commentId = null;
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> commentIdValidator.validateCommentId(commentId));
        assertEquals("commentId cannot be null", exception.getMessage());
    }
}
package faang.school.postservice.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PostIdValidatorTest {

    @InjectMocks
    private PostIdValidator postIdValidator;

    @Test
    void positivePostIdValidate() {
        Long postId = 1L;
        assertDoesNotThrow(() ->
                postIdValidator.postIdValidate(postId));
    }

    @Test
    public void negativePostIdValidate() {
        Long postId = null;
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> postIdValidator.postIdValidate(postId));
        assertEquals("Post id cannot be null", exception.getMessage());

    }
}
package faang.school.postservice.service.post;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostIdValidator;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostIdValidator postIdValidator;

    @InjectMocks
    private PostService postService;

    @Test
    void positiveFindPostById() {
        Long postId = 1L;

        Post post = Post.builder().id(1L).build();

        doNothing().when(postIdValidator).postIdValidate(postId);
        when(postRepository.findById(postId)).thenReturn(Optional.ofNullable(post));

        Post result = postService.findPostById(postId);

        verify(postIdValidator, times(1)).postIdValidate(postId);
        verify(postRepository, times(1)).findById(postId);

        assertNotNull(result);
        assertEquals(postId, result.getId());
    }

    @Test
    void negativeFindPostById() {
        Long postId = 1L;

        doNothing().when(postIdValidator).postIdValidate(postId);
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> postService.findPostById(postId));

        verify(postIdValidator, times(1)).postIdValidate(postId);
        verify(postRepository, times(1)).findById(postId);

        assertNotNull(exception);
        assertEquals("Post not found", exception.getMessage());
    }

    @Test
    void positiveExistsPost() {
        Long postId = 1L;
        doNothing().when(postIdValidator).postIdValidate(postId);
        when(postRepository.existsById(postId)).thenReturn(true);

        postService.existsPost(postId);

        verify(postIdValidator, times(1)).postIdValidate(postId);
        verify(postRepository, times(1)).existsById(postId);
    }

    @Test
    void negativeExistsPost() {
        doThrow(new IllegalArgumentException("Invalid post ID")).when(postIdValidator).postIdValidate(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> postService.existsPost(null));

        verify(postIdValidator, times(1)).postIdValidate(null);

        verify(postRepository, never()).existsById(null);

        Assertions.assertEquals("Invalid post ID", exception.getMessage());
    }
}
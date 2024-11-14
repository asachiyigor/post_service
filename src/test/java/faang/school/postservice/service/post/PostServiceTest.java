package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDraftCreateDto;
import faang.school.postservice.dto.post.PostDraftResponseDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.post.PostMapperImpl;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.album.AlbumService;
import faang.school.postservice.service.resource.ResourceService;
import faang.school.postservice.validator.dto.ProjectDtoValidator;
import faang.school.postservice.validator.dto.UserDtoValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @InjectMocks
    private PostService postService;
    @Mock
    private PostMapperImpl postMapper;
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserServiceClient userService;
    @Mock
    private ProjectServiceClient projectService;
    @Mock
    private AlbumService albumService;
    @Mock
    private ResourceService resourceService;
    @Mock
    private UserDtoValidator userDtoValidator;
    @Mock
    private ProjectDtoValidator projectDtoValidator;

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    static Stream<Object[]> validRequestsDraftDto() {
        return Stream.of(
                new Object[]{PostDraftCreateDto.builder()
                        .content("content")
                        .authorId(1L)
                        .build()},
                new Object[]{PostDraftCreateDto.builder()
                        .content("content")
                        .projectId(1L)
                        .build()},
                new Object[]{PostDraftCreateDto.builder()
                        .content("content")
                        .authorId(1L)
                        .albumsId(new ArrayList<>(List.of(1L, 2L)))
                        .build()},
                new Object[]{PostDraftCreateDto.builder()
                        .content("content")
                        .authorId(1L)
                        .resourcesId(new ArrayList<>(List.of(1L, 2L)))
                        .build()},
                new Object[]{PostDraftCreateDto.builder()
                        .content("content")
                        .authorId(1L)
                        .albumsId(new ArrayList<>(List.of(1L, 2L)))
                        .resourcesId(new ArrayList<>(List.of(1L, 2L)))
                        .build()}
        );
    }

    @ParameterizedTest
    @MethodSource("validRequestsDraftDto")
    void testCreateDraftPost_withValidInputDto_shouldCreateAndReturnPostDraftResponseDto(PostDraftCreateDto requestDto) {
        if (requestDto.getAuthorId() != null) {
            when(userService.getUser(anyLong())).thenReturn(new UserDto());
            doNothing().when(userDtoValidator).validateUserDto(new UserDto());
        }
        if (requestDto.getProjectId() != null) {
            when(projectService.getProject(anyLong())).thenReturn(new ProjectDto());
            doNothing().when(projectDtoValidator).validateProjectDto(new ProjectDto());
        }
        when(postMapper.toEntityFromDraftDto(requestDto)).thenReturn(new Post());
        if (requestDto.getAlbumsId() != null) {
            when(albumService.getAlbumsByIds(any())).thenReturn(List.of(new Album(), new Album()));
        }
        if (requestDto.getResourcesId() != null) {
            when(resourceService.getResourcesByIds(any())).thenReturn(List.of(new Resource(), new Resource()));
        }
        when(postRepository.save(any())).thenReturn(new Post());
        PostDraftResponseDto responseDto = mock(PostDraftResponseDto.class);
        when(postMapper.toDraftDtoFromPost(any(Post.class))).thenReturn(responseDto);

        PostDraftResponseDto result = postService.createDraftPost(requestDto);

        if (requestDto.getAuthorId() != null) {
            verify(userService, times(1)).getUser(anyLong());
            verify(userDtoValidator, times(1)).validateUserDto(new UserDto());
        }
        if (requestDto.getProjectId() != null) {
            verify(projectService, times(1)).getProject(anyLong());
            verify(projectDtoValidator, times(1)).validateProjectDto(new ProjectDto());
        }
        if (requestDto.getAlbumsId() != null) {
            verify(albumService, times(1)).getAlbumsByIds(any());
        }
        if (requestDto.getResourcesId() != null) {
            verify(resourceService, times(1)).getResourcesByIds(any());
        }
        verify(postMapper, times(1)).toEntityFromDraftDto(requestDto);
        verify(postRepository, times(1)).save(any());
        verify(postMapper, times(1)).toDraftDtoFromPost(any(Post.class));

        Set<ConstraintViolation<PostDraftCreateDto>> violations = validator.validate(requestDto);
        assertTrue(violations.isEmpty());
        assertNotNull(result);
    }

    static Stream<Object[]> invalidRequestsDraftDto() {
        return Stream.of(
                new Object[]{PostDraftCreateDto.builder()
                        .content("    ")
                        .authorId(1L)
                        .build()},
                new Object[]{PostDraftCreateDto.builder()
                        .authorId(1L)
                        .build()},
                new Object[]{PostDraftCreateDto.builder()
                        .content("content")
                        .build()},
                new Object[]{PostDraftCreateDto.builder()
                        .content("content")
                        .authorId(1L)
                        .projectId(1L)
                        .build()},
                new Object[]{PostDraftCreateDto.builder()
                        .content("content")
                        .authorId(-1L)
                        .build()},
                new Object[]{PostDraftCreateDto.builder()
                        .content("content")
                        .projectId(-2L)
                        .build()},
                new Object[]{PostDraftCreateDto.builder()
                        .content("content")
                        .authorId(-2L)
                        .projectId(-1L)
                        .build()},
                new Object[]{PostDraftCreateDto.builder()
                        .content("content")
                        .authorId(0L)
                        .build()},
                new Object[]{PostDraftCreateDto.builder()
                        .content("content")
                        .projectId(0L)
                        .build()},
                new Object[]{PostDraftCreateDto.builder()
                        .content("content")
                        .authorId(0L)
                        .projectId(0L)
                        .build()},
                new Object[]{PostDraftCreateDto.builder()
                        .content("content")
                        .projectId(1L)
                        .resourcesId(new ArrayList<>(List.of(1L, -2L)))
                        .build()},
                new Object[]{PostDraftCreateDto.builder()
                        .content("content")
                        .projectId(1L)
                        .albumsId(new ArrayList<>(List.of(1L, -2L)))
                        .build()},
                new Object[]{PostDraftCreateDto.builder()
                        .content("content")
                        .projectId(1L)
                        .albumsId(new ArrayList<>(List.of()))
                        .build()},
                new Object[]{PostDraftCreateDto.builder()
                        .content("content")
                        .projectId(1L)
                        .resourcesId(new ArrayList<>(List.of()))
                        .build()},
                new Object[]{PostDraftCreateDto.builder()
                        .content("content")
                        .projectId(1L)
                        .albumsId(new ArrayList<>(List.of()))
                        .resourcesId(new ArrayList<>(List.of()))
                        .build()}
        );
    }

    @ParameterizedTest
    @MethodSource("invalidRequestsDraftDto")
    void testCreateDraftPost_withInvalidInputDto_shouldThrowConstraintViolationException(PostDraftCreateDto dto) {
        Set<ConstraintViolation<PostDraftCreateDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        verify(postRepository, times(0)).save(any());
    }

    @Test
    void testCreateDraftPost_withNotExistsAuthor_shouldThrowEntityNotFoundException() {
        PostDraftCreateDto requestDto = PostDraftCreateDto.builder()
                .content("content")
                .authorId(1L)
                .build();

        when(userService.getUser(anyLong())).thenReturn(null);
        doThrow(new EntityNotFoundException("User not found"))
                .when(userDtoValidator)
                .validateUserDto(null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> postService.createDraftPost(requestDto)
        );

        verify(userService, times(1)).getUser(anyLong());
        assertEquals("User not found", exception.getMessage());
        verify(postRepository, times(0)).save(any());
    }

    @Test
    void testCreateDraftPost_withNotExistsProject_shouldThrowEntityNotFoundException() {
        PostDraftCreateDto requestDto = PostDraftCreateDto.builder()
                .content("content")
                .projectId(1L)
                .build();

        when(projectService.getProject(anyLong())).thenReturn(null);
        doThrow(new EntityNotFoundException("Project not found"))
                .when(projectDtoValidator)
                .validateProjectDto(null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> postService.createDraftPost(requestDto)
        );

        verify(projectService, times(1)).getProject(anyLong());
        assertEquals("Project not found", exception.getMessage());
        verify(postRepository, times(0)).save(any());
    }


    @Test
    void testPublishPost_withValidPostId_shouldPublishAndReturnPostResponseDto() {
        long postId = 1L;
        PostResponseDto responseDto = PostResponseDto.builder()
                .id(1L)
                .content("content")
                .authorId(1L)
                .published(true)
                .build();

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(new Post()));
        when(postRepository.save(any())).thenReturn(new Post());
        when(postMapper.toDtoFromPost(any(Post.class))).thenReturn(responseDto);

        PostResponseDto result = postService.publishPost(postId);

        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).save(any());
        verify(postMapper, times(1)).toDtoFromPost(any(Post.class));

        assertNotNull(result);
        assertEquals(result, responseDto);
    }

    @Test
    void testPublishPost_withNotExistsPost_shouldThrowIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            postService.publishPost(anyLong());
        });

        assertTrue(exception.getMessage().contains("Post not found"));
        verify(postRepository, times(0)).save(any());
    }

    @Test
    void testPublishPost_withPostAlreadyPublished_shouldThrowIllegalArgumentException() {
        long postId = 1L;
        Post post = Post.builder()
                .id(1L)
                .content("content")
                .authorId(1L)
                .published(true)
                .build();

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> postService.publishPost(postId)
        );

        assertTrue(exception.getMessage().contains("Post is already published"));

        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(0)).save(any());
    }


    @Test
    void testUpdatePost_withValidPostIdAndDto_shouldUpdateAndReturnPostResponseDto() {
        long postId = 1L;
        PostUpdateDto requestDto = PostUpdateDto.builder()
                .content("contentUpdate")
                .build();
        PostResponseDto responseDto = PostResponseDto.builder()
                .id(1L)
                .content("contentUpdate")
                .authorId(1L)
                .build();
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(new Post()));
        when(postRepository.save(any())).thenReturn(new Post());
        when(postMapper.toDtoFromPost(any(Post.class))).thenReturn(responseDto);

        PostResponseDto result = postService.updatePost(postId, requestDto);

        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).save(any());
        verify(postMapper, times(1)).toDtoFromPost(any(Post.class));

        assertNotNull(result);
        assertEquals(result, responseDto);
    }

    @Test
    void testUpdatePost_withInValidRequestDto_shouldThrowConstraintViolationException() {
        PostUpdateDto requestDto = PostUpdateDto.builder().build();

        Set<ConstraintViolation<PostUpdateDto>> violations = validator.validate(requestDto);

        assertFalse(violations.isEmpty());
        verify(postRepository, times(0)).save(any());
    }

    @Test
    void testUpdatePost_withNotExistsPost_shouldThrowIllegalArgumentException() {
        PostUpdateDto requestDto = PostUpdateDto.builder().content("content").build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            postService.updatePost(anyLong(), requestDto);
        });

        assertTrue(exception.getMessage().contains("Post not found"));
        verify(postRepository, times(0)).save(any());
    }

    @Test
    void testDeletePost_withValidPostId_shouldDeleteAndReturnPostResponseDto() {
        long postId = 1L;
        PostResponseDto responseDto = PostResponseDto.builder()
                .id(1L)
                .content("contentUpdate")
                .authorId(1L)
                .deleted(true)
                .build();
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(new Post()));
        when(postRepository.save(any())).thenReturn(new Post());
        when(postMapper.toDtoFromPost(any(Post.class))).thenReturn(responseDto);

        PostResponseDto result = postService.deletePost(postId);

        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).save(any());
        verify(postMapper, times(1)).toDtoFromPost(any(Post.class));

        assertNotNull(result);
        assertEquals(result, responseDto);
    }

    @Test
    void testDeletePost_withNotExistsPost_shouldThrowIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            postService.deletePost(anyLong());
        });

        assertTrue(exception.getMessage().contains("Post not found"));
        verify(postRepository, times(0)).save(any());
    }

    @Test
    void testGetPost_withValidPostId_returnPostResponseDto() {
        long postId = 1L;
        PostResponseDto responseDto = PostResponseDto.builder()
                .id(1L)
                .content("content")
                .authorId(1L)
                .build();
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(new Post()));
        when(postMapper.toDtoFromPost(any(Post.class))).thenReturn(responseDto);

        PostResponseDto result = postService.getPost(postId);

        verify(postRepository, times(1)).findById(postId);
        verify(postMapper, times(1)).toDtoFromPost(any(Post.class));

        assertNotNull(result);
        assertEquals(result, responseDto);
    }

    @Test
    void testGetPost_withNotExistsPost_shouldThrowIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            postService.getPost(anyLong());
        });

        assertTrue(exception.getMessage().contains("Post not found"));
        verify(postRepository, times(0)).save(any());
    }

    @Test
    void testGetDraftPostsByUserIdSortedCreatedAtDesc_Positive() {
    }

    @Test
    void testGetDraftPostsByProjectIdSortedCreatedAtDesc_Positive() {
    }

    @Test
    void testGetPublishPostsByUserIdSortedCreatedAtDesc_Positive() {
    }

    @Test
    void testGetPublishPostsByProjectIdSortedCreatedAtDesc_Positive() {
    }
}
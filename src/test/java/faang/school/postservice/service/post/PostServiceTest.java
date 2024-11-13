package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDraftCreateDto;
import faang.school.postservice.dto.post.PostDraftResponseDto;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

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

    @Test
    void testCreateDraftPostWithAuthor_Positive() {
        PostDraftCreateDto requestDto = PostDraftCreateDto.builder()
                .content("content")
                .authorId(1L)
                .albumsId(new ArrayList<>(List.of(1L, 2L)))
                .build();
        PostDraftResponseDto responseDto = PostDraftResponseDto.builder()
                .id(1L)
                .content("content")
                .authorId(1L)
                .albumsIds(new ArrayList<>(List.of(1L, 2L)))
                .published(false)
                .deleted(false)
                .build();
        when(userService.getUser(anyLong())).thenReturn(new UserDto());
        doNothing().when(userDtoValidator).validateUserDto(new UserDto());
        when(postMapper.toEntityFromDraftDto(requestDto)).thenReturn(new Post());
        when(albumService.getAlbumsByIds(any())).thenReturn(List.of(new Album(), new Album()));
        when(postRepository.save(any())).thenReturn(new Post());
        when(postMapper.toDraftDtoFromPost(any())).thenReturn(responseDto);

        PostDraftResponseDto result = postService.createDraftPost(requestDto);

        verify(userService, times(1)).getUser(anyLong());
        verify(userDtoValidator, times(1)).validateUserDto(new UserDto());
        verify(postMapper, times(1)).toEntityFromDraftDto(requestDto);
        verify(albumService, times(1)).getAlbumsByIds(any());
        verify(postRepository, times(1)).save(any());
        verify(postMapper, times(1)).toDraftDtoFromPost(any());

        assertNotNull(result);
        assertEquals(responseDto.getId(), result.getId());
        assertEquals(responseDto.isPublished(), result.isPublished());
        assertEquals(responseDto.getAlbumsIds().size(), result.getAlbumsIds().size());


//        doThrow(new EntityNotFoundException("User not found"))
//                .when(userDtoValidator)
//                .validateUserDto(null);

    }

    @Test
    void testCreateDraftPostWithProject_Positive() {
        PostDraftCreateDto requestDto = PostDraftCreateDto.builder()
                .content("content")
                .projectId(1L)
                .resourcesId(new ArrayList<>(List.of(1L, 2L)))
                .build();
        PostDraftResponseDto responseDto = PostDraftResponseDto.builder()
                .id(1L)
                .content("content")
                .projectId(1L)
                .resourcesIds(new ArrayList<>(List.of(1L, 2L)))
                .published(false)
                .deleted(false)
                .build();
        when(projectService.getProject(anyLong())).thenReturn(new ProjectDto());
        doNothing().when(projectDtoValidator).validateProjectDto(new ProjectDto());
        when(postMapper.toEntityFromDraftDto(requestDto)).thenReturn(new Post());
        when(resourceService.getResourcesByIds(any())).thenReturn(List.of(new Resource(), new Resource()));
        when(postRepository.save(any())).thenReturn(new Post());
        when(postMapper.toDraftDtoFromPost(any())).thenReturn(responseDto);

        PostDraftResponseDto result = postService.createDraftPost(requestDto);

        verify(projectService, times(1)).getProject(anyLong());
        verify(projectDtoValidator, times(1)).validateProjectDto(new ProjectDto());
        verify(postMapper, times(1)).toEntityFromDraftDto(requestDto);
        verify(resourceService, times(1)).getResourcesByIds(any());
        verify(postRepository, times(1)).save(any());
        verify(postMapper, times(1)).toDraftDtoFromPost(any());

        assertNotNull(result);
        assertEquals(responseDto.getId(), result.getId());
        assertEquals(responseDto.isPublished(), result.isPublished());
        assertEquals(responseDto.getResourcesIds().size(), result.getResourcesIds().size());


//        doThrow(new EntityNotFoundException("User not found"))
//                .when(userDtoValidator)
//                .validateUserDto(null);

    }

    @Test
    void testCreateDraftPostWithAuthorAndProject_Negative() {
        PostDraftCreateDto requestDto = PostDraftCreateDto.builder()
                .content("content")
                .authorId(1L)
                .projectId(1L)
                .build();

        assertThrows(IllegalArgumentException.class, () -> postService.createDraftPost(requestDto));
    }

    @Test
    void testPublishPos_Positive() {
    }

    @Test
    void testUpdatePost_Positive() {
    }

    @Test
    void testDeletePostById_Positive() {
    }

    @Test
    void testGetPost_Positive() {
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
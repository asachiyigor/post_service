package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.*;
import faang.school.postservice.service.post.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = PostController.class)
class PostControllerTest {
    private final static String URL_DRAFT = "/api/v1/posts/draft";
    private final static String URL_DRAFT_FILES = "/api/v1/posts/draft/files";
    private final static String URL_PUBLISH = "/api/v1/posts/{postId}/publish";
    private final static String URL_UPDATE = "/api/v1/posts/{postId}/update";
    private final static String URL_DELETE = "/api/v1/posts/{postId}/delete";
    private final static String URL_GET = "/api/v1/posts/{postId}";
    private final static String URL_GET_ALL_DRAFT_BY_USER_ID = "/api/v1/posts/user/{userId}/drafts";
    private final static String URL_GET_ALL_DRAFT_BY_PROJECT_ID = "/api/v1/posts/project/{projectId}/drafts";
    private final static String URL_GET_ALL_PUBLISH_BY_USER_ID = "/api/v1/posts/user/{userId}/publishes";
    private final static String URL_GET_ALL_PUBLISH_BY_PROJECT_ID = "/api/v1/posts/project/{projectId}/publishes";

    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @MockBean
    private PostService service;

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @MethodSource("successRequestsDraftDto")
    void testCreateDraftPostSuccess(PostDraftCreateDto requestDto) throws Exception {
        PostDraftResponseDto responseDto = PostDraftResponseDto.builder()
                .id(1L)
                .content("content")
                .authorId(1L)
                .published(false)
                .build();

        when(service.createDraftPost(requestDto)).thenReturn(responseDto);

        mockMvc.perform(post(URL_DRAFT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(requestDto)))
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(responseDto)))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @MethodSource("invalidRequestsDraftDto")
    void testCreateDraftPostFail(PostDraftCreateDto requestDto) throws Exception {
        mockMvc.perform(post(URL_DRAFT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPublishPostSuccess() throws Exception {
        long postId = 1L;
        PostResponseDto responseDto = PostResponseDto.builder()
                .id(1L)
                .content("content")
                .authorId(1L)
                .published(true)
                .build();
        when(service.publishPost(1L)).thenReturn(responseDto);

        mockMvc.perform(put(URL_PUBLISH, postId))
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(responseDto)))
                .andExpect(status().isOk());
    }

    @Test
    void updatePost() throws Exception {
        long postId = 1L;
        PostUpdateDto requestDto = PostUpdateDto.builder()
                .content("contentUpdate")
                .build();

        PostResponseDto responseDto = PostResponseDto.builder()
                .id(1L)
                .content("contentUpdate")
                .authorId(1L)
                .published(true)
                .build();
        when(service.updatePost(postId, requestDto)).thenReturn(responseDto);

        mockMvc.perform(put(URL_UPDATE, postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(requestDto)))
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(responseDto)))
                .andExpect(status().isOk());
    }

    @Test
    void deletePostById() throws Exception {
        long postId = 1L;

        when(service.deletePost(postId)).thenReturn(null);

        mockMvc.perform(delete(URL_DELETE, postId))
                .andExpect(status().isOk());
    }

    @Test
    void getPost() throws Exception {
        long postId = 1L;
        PostResponseDto responseDto = PostResponseDto.builder()
                .id(1L)
                .content("content")
                .authorId(1L)
                .published(true)
                .deleted(false)
                .build();

        when(service.getPost(postId)).thenReturn(responseDto);

        mockMvc.perform(get(URL_GET, postId))
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(responseDto)))
                .andExpect(status().isOk());
    }

    @Test
    void getAllDraftNonDelPostsByUserIdSortedCreatedAtDesc() throws Exception {
        long userId = 1L;
        List<PostDraftResponseDto> responsesDto = Arrays.asList(
                PostDraftResponseDto.builder()
                        .id(1L)
                        .content("content")
                        .authorId(1L)
                        .published(false)
                        .deleted(false)
                        .build(),
                PostDraftResponseDto.builder()
                        .id(5L)
                        .content("content")
                        .authorId(1L)
                        .published(false)
                        .deleted(false)
                        .build()
        );

        when(service.getDraftPostsByUserIdSortedCreatedAtDesc(userId)).thenReturn(responsesDto);

        mockMvc.perform(get(URL_GET_ALL_DRAFT_BY_USER_ID, userId))
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(responsesDto)))
                .andExpect(status().isOk());
    }

    @Test
    void getAllDraftNonDelPostsByProjectIdSortedCreatedAtDesc() throws Exception {
        long projectId = 1L;
        List<PostDraftResponseDto> responsesDto = Arrays.asList(
                PostDraftResponseDto.builder()
                        .id(1L)
                        .content("content")
                        .projectId(1L)
                        .published(false)
                        .deleted(false)
                        .build(),
                PostDraftResponseDto.builder()
                        .id(5L)
                        .content("content")
                        .projectId(1L)
                        .published(false)
                        .deleted(false)
                        .build()
        );

        when(service.getDraftPostsByProjectIdSortedCreatedAtDesc(projectId)).thenReturn(responsesDto);

        mockMvc.perform(get(URL_GET_ALL_DRAFT_BY_PROJECT_ID, projectId))
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(responsesDto)))
                .andExpect(status().isOk());
    }

    @Test
    void getAllPublishNonDelPostsByUserIdSortedCreatedAtDesc() throws Exception {
        long userId = 1L;
        List<PostResponseDto> responsesDto = Arrays.asList(
                PostResponseDto.builder()
                        .id(1L)
                        .content("content")
                        .authorId(1L)
                        .published(true)
                        .deleted(false)
                        .build(),
                PostResponseDto.builder()
                        .id(5L)
                        .content("content")
                        .authorId(1L)
                        .published(true)
                        .deleted(false)
                        .build()
        );

        when(service.getPublishPostsByUserIdSortedCreatedAtDesc(userId)).thenReturn(responsesDto);

        mockMvc.perform(get(URL_GET_ALL_PUBLISH_BY_USER_ID, userId))
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(responsesDto)))
                .andExpect(status().isOk());
    }

    @Test
    void getAllPublishNonDelPostsByProjectIdSortedCreatedAtDesc() throws Exception {
        long projectId = 1L;
        List<PostResponseDto> responsesDto = Arrays.asList(
                PostResponseDto.builder()
                        .id(1L)
                        .content("content")
                        .projectId(1L)
                        .published(true)
                        .deleted(false)
                        .build(),
                PostResponseDto.builder()
                        .id(5L)
                        .content("content")
                        .projectId(1L)
                        .published(true)
                        .deleted(false)
                        .build()
        );

        when(service.getPublishPostsByProjectIdSortedCreatedAtDesc(anyLong())).thenReturn(responsesDto);

        mockMvc.perform(get(URL_GET_ALL_PUBLISH_BY_PROJECT_ID, projectId))
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(responsesDto)))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateDraftWithFiles() throws Exception {
        PostDraftWithFilesCreateDto dto = PostDraftWithFilesCreateDto.builder()
                .albumsId(List.of(1L, 2L, 3L))
                .content("Hello World")
                .projectId(2L)
                .build();
        PostDraftResponseDto responseDto = PostDraftResponseDto.builder()
                .id(1L)
                .content("Hello World")
                .projectId(2L)
                .authorId(1L)
                .albumsIds(List.of(1L, 2L, 3L))
                .build();

        MockMultipartFile dtoPart = new MockMultipartFile(
                "dto",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                OBJECT_MAPPER.writeValueAsBytes(dto)
        );

        MockMultipartFile file1 = new MockMultipartFile(
                "files",
                "test.jpg",
                "image/jpeg",
                "test".getBytes(StandardCharsets.UTF_8)
        );

        MockMultipartFile file2 = new MockMultipartFile(
                "files",
                "test2.png",
                "image/png",
                "testes".getBytes(StandardCharsets.UTF_8)
        );

        when(service.createDraftPostWithFiles(any(PostDraftWithFilesCreateDto.class), any())).thenReturn(responseDto);

        mockMvc.perform(multipart(URL_DRAFT_FILES)
                        .file(dtoPart)
                        .file(file1)
                        .file(file2)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(responseDto)));
    }

    static Stream<Object[]> successRequestsDraftDto() {
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
}
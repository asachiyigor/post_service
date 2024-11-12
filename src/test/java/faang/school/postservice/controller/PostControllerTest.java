package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDraftCreateDto;
import faang.school.postservice.dto.post.PostDraftResponseDto;
import faang.school.postservice.service.post.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = PostController.class)
class PostControllerTest {
    private final static String URL_DRAFT = "/api/v1/posts/draft";
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
    private PostService postService;

    @Autowired
    private MockMvc mockMvc;

    static Stream<Object[]> successRequestsDraftDto(){
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
    @MethodSource("successRequestsDraftDto")
    void testCreateDraftPostSuccess(PostDraftCreateDto requestDto ) throws Exception {
        PostDraftResponseDto responseDto = PostDraftResponseDto.builder()
                .id(1L)
                .content("content")
                .authorId(1L)
                .published(false)
                .build();

        when(postService.createDraftPost(requestDto)).thenReturn(responseDto);

        mockMvc.perform(post(URL_DRAFT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(requestDto)))
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(responseDto)))
                .andExpect(status().isOk());
    }

    static Stream<Object[]> invalidRequestsDraftDto(){
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
    void testCreateDraftPostFail(PostDraftCreateDto requestDto) throws Exception {
        mockMvc.perform(post(URL_DRAFT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }



    @Test
    void publishPost() {
    }

    @Test
    void updatePost() {
    }

    @Test
    void deletePostById() {
    }

    @Test
    void getPostById() {
    }

    @Test
    void getAllDraftNonDelPostsByUserIdSortedCreatedAtDesc() {
    }

    @Test
    void getAllDraftNonDelPostsByProjectIdSortedCreatedAtDesc() {
    }

    @Test
    void getAllPublishNonDelPostsByUserIdSortedCreatedAtDesc() {
    }

    @Test
    void getAllPublishNonDelPostsByProjectIdSortedCreatedAtDesc() {
    }
}
package faang.school.postservice.controller.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.PostDraftResponseDto;
import faang.school.postservice.dto.post.PostDraftWithFilesCreateDto;
import faang.school.postservice.service.post.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = PostController.class)
public class PostTest {

    private final static String URL_DRAFT_FILES = "/api/v1/posts/draft/files";

    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

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

        when(postService.createDraftPostWithFiles(any(PostDraftWithFilesCreateDto.class), any())).thenReturn(responseDto);

        mockMvc.perform(multipart(URL_DRAFT_FILES)
                        .file(dtoPart)
                        .file(file1)
                        .file(file2)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(responseDto)));
    }

}

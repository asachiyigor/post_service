package faang.school.postservice.controller.like;

import faang.school.postservice.dto.like.LikeDtoForComment;
import faang.school.postservice.dto.like.LikeDtoForPost;
import faang.school.postservice.dto.like.ResponseLikeDto;
import faang.school.postservice.service.like.LikeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest
@ContextConfiguration(classes = {LikeController.class})
class LikeControllerTest {
    private static final String URL_FOR_POSTS = "/like/post";
    private static final String URL_FOR_COMMENTS = "/like/comment";
    @MockBean
    private LikeService likeService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void likePostValidData() throws Exception {
        LikeDtoForPost likeDtoForPost = setUpLikeDtoForPost(5L, 1L);
        ResponseLikeDto responseLikeDto = responseLikeDtoForPost(1L, 1L);

        when(likeService.addLikeByPost(likeDtoForPost)).thenReturn(responseLikeDto);

        mockMvc.perform(post(URL_FOR_POSTS).contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(likeDtoForPost)))
                .andExpect(status().isOk());

        verify(likeService, times(1)).addLikeByPost(likeDtoForPost);
    }

    @Test
    void likePostInvalidData() throws Exception {
        LikeDtoForPost invalidLikeDto = setUpLikeDtoForPost(-8L, 0L);

        mockMvc.perform(post(URL_FOR_POSTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(invalidLikeDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteLikeFromPost() throws Exception {
        LikeDtoForPost likeDtoForPost = setUpLikeDtoForPost(10L, 1L);

        doNothing().when(likeService).deleteLikeFromPost(likeDtoForPost);

        mockMvc.perform(delete(URL_FOR_POSTS).contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(likeDtoForPost)))
                .andExpect(status().isNoContent());
        verify(likeService, times(1)).deleteLikeFromPost(likeDtoForPost);
    }

    @Test
    void deleteLikeFromPostInvalidData() throws Exception {
        LikeDtoForPost invalidLikeDto = setUpLikeDtoForPost(-1L, 0L);

        mockMvc.perform(post(URL_FOR_POSTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(invalidLikeDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void likeCommentValidData() throws Exception {
        LikeDtoForComment likeDtoForComment = setUpLikeDtoForComment(6L, 9L);
        ResponseLikeDto responseLikeDto = responseLikeDtoForComment(17L, 9L);

        when(likeService.addLikeByComment(likeDtoForComment)).thenReturn(responseLikeDto);

        mockMvc.perform(post(URL_FOR_COMMENTS).contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(likeDtoForComment)))
                .andExpect(status().isOk());

        verify(likeService, times(1)).addLikeByComment(likeDtoForComment);
    }

    @Test
    void likeCommentInvalidData() throws Exception {
        LikeDtoForComment likeDtoForComment = setUpLikeDtoForComment(-10L, 0L);

        mockMvc.perform(post(URL_FOR_COMMENTS).contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(likeDtoForComment)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteLikeFromComment() throws Exception {
        LikeDtoForComment likeDtoForComment = setUpLikeDtoForComment(5L, 1L);

        doNothing().when(likeService).deleteLikeFromComment(likeDtoForComment);

        mockMvc.perform(delete(URL_FOR_COMMENTS).contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(likeDtoForComment)))
                .andExpect(status().isNoContent());

        verify(likeService, times(1)).deleteLikeFromComment(likeDtoForComment);
    }

    @Test
    void deleteLikeFromCommentInvalidData() throws Exception {
        LikeDtoForComment likeDtoForComment = setUpLikeDtoForComment(-15L, 0L);

        mockMvc.perform(post(URL_FOR_COMMENTS).contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(likeDtoForComment)))
                .andExpect(status().isBadRequest());
    }

    private LikeDtoForComment setUpLikeDtoForComment(Long userId, Long commentId) {
        return LikeDtoForComment.builder()
                .userId(userId)
                .commentId(commentId)
                .build();
    }

    private LikeDtoForPost setUpLikeDtoForPost(Long userId, Long postId) {
        return LikeDtoForPost.builder()
                .userId(userId)
                .postId(postId)
                .build();
    }

    private ResponseLikeDto responseLikeDtoForComment(Long userId, Long commentId) {
        return ResponseLikeDto.builder()
                .userId(userId)
                .commentId(commentId)
                .build();
    }

    private ResponseLikeDto responseLikeDtoForPost(Long userId, Long postId) {
        return ResponseLikeDto.builder()
                .userId(userId)
                .postId(postId)
                .build();
    }
}
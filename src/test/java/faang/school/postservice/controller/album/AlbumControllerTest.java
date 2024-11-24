package faang.school.postservice.controller.album;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.service.album.AlbumService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
@ContextConfiguration(classes = {AlbumController.class})
class AlbumControllerTest {

  private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @MockBean
  private AlbumService albumService;

  @Autowired
  private MockMvc mockMvc;

  @Test
  @DisplayName("Should return added album")
  void addAlbum() throws Exception {
    AlbumDto albumDto = AlbumDto.builder()
        .title("Title")
        .description("Description")
        .authorId(1L)
        .build();

    AlbumDto expectedResponse = AlbumDto.builder()
        .id(1L)
        .title("Title")
        .description("Description")
        .authorId(1L)
        .build();

    when(albumService.add(albumDto)).thenReturn(expectedResponse);

    mockMvc.perform(post("/albums/add")
        .contentType(MediaType.APPLICATION_JSON)
        .content(OBJECT_MAPPER.writeValueAsString(albumDto)))
        .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(expectedResponse)))
        .andExpect(status().isOk());
    verify(albumService, times(1)).add(albumDto);
  }

  @Test
  void getAlbumById() {
  }

  @Test
  void updateAlbum() {
  }

  @Test
  void removeAlbum() {
  }

  @Test
  void addPostToAlbum() {
  }

  @Test
  void removePostFromAlbum() {
  }

  @Test
  void addAlbumToFavorites() {
  }

  @Test
  void removeAlbumToFavorites() {
  }

  @Test
  void getUserAlbums() {
  }

  @Test
  void getUserFavoritesAlbums() {
  }

  @Test
  @DisplayName("Should return filtered albums")
  void getAllAlbums() {
  }
}
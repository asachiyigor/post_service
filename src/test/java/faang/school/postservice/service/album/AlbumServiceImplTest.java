package faang.school.postservice.service.album;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.comment.DataValidationException;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.repository.AlbumRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AlbumServiceImplTest {

  @InjectMocks
  private AlbumServiceImpl albumService;

  @Mock
  private AlbumRepository albumRepository;

  @Mock
  private UserServiceClient userServiceClient;

  @Spy
  private AlbumMapper albumMapper = Mappers.getMapper(AlbumMapper.class);

  @Test
  @DisplayName("Should throw DataValidationException when author already has album with the title")
  void testAddAlbumWithNameAlreadyExists() {
    AlbumDto testAlbumDto = getTestAlbumDto();
    String title = testAlbumDto.getTitle();
    Long authorId = testAlbumDto.getAuthorId();

    when(albumRepository.existsByTitleAndAuthorId(title, authorId)).thenReturn(Boolean.TRUE);

    DataValidationException exception = assertThrows(DataValidationException.class, () -> albumService.add(testAlbumDto));

    verify(albumRepository, times(1)).existsByTitleAndAuthorId(title, authorId);

    assertEquals(String.format("Author with ID %d already has album with Title %s", authorId, title), exception.getMessage());
  }

  @Test
  @DisplayName("Should throw EntityNotFoundException when author does not exist")
  void testAddAlbumWithNonExistingAuthor() {
    AlbumDto testAlbumDto = getTestAlbumDto();
    String title = testAlbumDto.getTitle();
    Long authorId = testAlbumDto.getAuthorId();

    when(albumRepository.existsByTitleAndAuthorId(title, authorId)).thenReturn(Boolean.FALSE);
    when(userServiceClient.getUser(authorId)).thenReturn(null);

    EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> albumService.add(testAlbumDto));

    verify(albumRepository, times(1)).existsByTitleAndAuthorId(title, authorId);
    verify(userServiceClient, times(1)).getUser(authorId);

    assertEquals(String.format("Author with ID %d not found", authorId), exception.getMessage());
  }

  private AlbumDto getTestAlbumDto() {
    return AlbumDto.builder()
        .id(1L)
        .title("Test Album Title")
        .description("Test Album Description")
        .authorId(1L)
        .build();
  }
}
package faang.school.postservice.service.album;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.exception.comment.DataValidationException;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class AlbumServiceImpl implements AlbumService {

  private final AlbumRepository albumRepository;
  private final UserServiceClient userServiceClient;
  private final AlbumMapper albumMapper;

  public List<Album> getAlbumsByIds(List<Long> albumsIds) {
    if (albumsIds == null) {
      return null;
    }
    return albumRepository.findAllById(albumsIds);
  }

  @Override
  public AlbumDto add(AlbumDto albumDto) {
    validate(albumDto);
    Album album = albumMapper.toEntity(albumDto);
    album.setPosts(new ArrayList<>());
    album = albumRepository.save(album);
    log.info("New album with ID {} was created", album.getId());
    return albumMapper.toDto(album);
  }

  private void validate(AlbumDto albumDto) {
    long authorId = albumDto.getAuthorId();
    String title = albumDto.getTitle();
    if (albumRepository.existsByTitleAndAuthorId(title, authorId)) {
      throw new DataValidationException(
          String.format("Author with ID %d already has album with Title %s", authorId, title));
    }
    if (userServiceClient.getUser(authorId) == null) {
      throw new EntityNotFoundException("Author with ID %d not found");
    }
  }

}

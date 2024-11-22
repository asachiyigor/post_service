package faang.school.postservice.service.album;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.exception.comment.DataValidationException;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.album.filter.AlbumFilter;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class AlbumServiceImpl implements AlbumService {

  private final AlbumRepository albumRepository;
  private final PostRepository postRepository;
  private final UserServiceClient userServiceClient;
  private final AlbumMapper albumMapper;
  private final List<AlbumFilter> albumFilters;

  public List<Album> getAlbumsByIds(List<Long> albumsIds) {
    if (albumsIds == null) {
      return null;
    }
    return albumRepository.findAllById(albumsIds);
  }

  @Override
  public AlbumDto add(AlbumDto albumDto) {
    validateAlbumDto(albumDto);
    Album album = albumMapper.toEntity(albumDto);
    album.setPosts(new ArrayList<>());
    album = albumRepository.save(album);
    log.info("New album with ID {} was created", album.getId());
    return albumMapper.toDto(album);
  }

  @Override
  public AlbumDto getAlbumById(Long id) {
    Album album = findAlbumById(id);
    return albumMapper.toDto(album);
  }

  @Override
  public void addPost(long albumId, long postId, long userId) {
    Album album = findAlbumById(albumId);
    Post post = findPostById(postId);
    validateUserAccess(album.getAuthorId(), userId);
    album.addPost(post);
    albumRepository.save(album);
    log.info("The post {} was added to the album {}", post.getContent(), album.getTitle());
  }

  @Override
  public void removePost(long albumId, long postId, long userId) {
    Album album = findAlbumById(albumId);
    Post post = findPostById(postId);
    validateUserAccess(album.getAuthorId(), userId);
    album.removePost(postId);
    albumRepository.save(album);
    log.info("The post {} was deleted from the album {}", post.getContent(), album.getTitle());
  }

  @Override
  public Album findAlbumById(Long id) {
    return albumRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Album does not exist"));
  }

  @Override
  public Post findPostById(Long id) {
    return postRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Post does not exist"));
  }

  @Transactional
  @Override
  public void addAlbumToFavorites(long albumId, long userId) {
    Album album = findAlbumById(albumId);
    validateUser(userId);
    validateUserAccess(album.getAuthorId(), userId);
    albumRepository.addAlbumToFavorites(albumId, userId);
    log.info("The album {} was added to favorites", findAlbumById(albumId).getTitle());
  }

  @Transactional
  @Override
  public void removeAlbumToFavorites(long albumId, long userId) {
    Album album = findAlbumById(albumId);
    validateUser(userId);
    validateUserAccess(album.getAuthorId(), userId);
    albumRepository.deleteAlbumFromFavorites(albumId, userId);
    log.info("The album {} was removed from favorites", findAlbumById(albumId).getTitle());
  }

  @Override
  public List<AlbumDto> getAlbumsByFilter(Long userId, AlbumFilterDto albumFilterDto) {
    Stream<Album> allAlbums = albumRepository.findAll().stream();
    return albumFilters.stream()
        .filter(albumFilter -> albumFilter.isApplicable(albumFilterDto))
//        .flatMap(albumFilter -> albumFilter.apply(allAlbums, albumFilterDto))
        .reduce(allAlbums, (stream,
                albumFilter) -> albumFilter.apply(stream, albumFilterDto),
            (s1, s2) -> s1)
        .map(albumMapper::toDto)
        .toList();
  }

  private void validateUserAccess(long albumAuthorId, long userId) {
    if (albumAuthorId != userId) {
      throw new DataValidationException("Only owner can delete post from this album");
    }
  }

  private void validateAlbumDto(AlbumDto albumDto) {
    long authorId = albumDto.getAuthorId();
    String title = albumDto.getTitle();
    if (albumRepository.existsByTitleAndAuthorId(title, authorId)) {
      throw new DataValidationException(
          String.format("Author with ID %d already has album with Title %s", authorId, title));
    }
    if (userServiceClient.getUser(authorId) == null) {
      throw new EntityNotFoundException(String.format("Author with ID %d not found", authorId));
    }
  }

  void validateUser(long userId) {
    if (userServiceClient.getUser(userId) == null) {
      throw new EntityNotFoundException(String.format("Author with ID %d not found", userId));
    }
  }

  void validateAlbum(long albumId) {
    findAlbumById(albumId);
  }

}

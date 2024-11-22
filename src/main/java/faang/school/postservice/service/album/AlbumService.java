package faang.school.postservice.service.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;

import faang.school.postservice.model.Post;
import jakarta.validation.Valid;
import java.util.List;

public interface AlbumService {

  public List<Album> getAlbumsByIds(List<Long> albumsIds);

  AlbumDto add(@Valid AlbumDto albumDto);

  AlbumDto getAlbumById(Long id);

  void addPost(long albumId, long postId, long userId);

  void removePost(long albumId, long postId, long userId);

  Album findAlbumById(Long id);

  Post findPostById(Long id);

  void addAlbumToFavorites(long albumId, long userId);

  void removeAlbumToFavorites(long albumId, long userId);

  List<AlbumDto> getAlbumsByFilter(Long userId, AlbumFilterDto albumFilterDto);
}

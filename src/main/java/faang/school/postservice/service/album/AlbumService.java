package faang.school.postservice.service.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.model.Album;

import jakarta.validation.Valid;
import java.util.List;

public interface AlbumService {

  public List<Album> getAlbumsByIds(List<Long> albumsIds);

  AlbumDto add(@Valid AlbumDto albumDto);
}

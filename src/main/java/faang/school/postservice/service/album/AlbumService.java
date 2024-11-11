package faang.school.postservice.service.album;

import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AlbumService {
    private final AlbumRepository albumRepository;

    public List<Album> getAlbumsByIds(List<Long> albumsIds) {
        if (albumsIds == null) {
            return null;
        }
        return albumRepository.findAllById(albumsIds);
    }

}

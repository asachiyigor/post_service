package faang.school.postservice.controller.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.service.album.AlbumService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/albums")
public class AlbumController {

  private final AlbumService albumService;

  @PostMapping("/add")
  public AlbumDto addAlbum(@Valid @RequestBody AlbumDto albumDto) {
    return albumService.add(albumDto);
  }

  @GetMapping("/{id}")
  public AlbumDto getAlbumById(@PathVariable long id) {
    return albumService.getAlbumById(id);
  }

  @PutMapping("/update/{userId}")
  public AlbumDto updateAlbum(@PathVariable long userId, @RequestBody AlbumDto albumDto) {
    return albumService.update(userId, albumDto);
  }

  @DeleteMapping("/delete/{userId}")
  public void removeAlbum(@PathVariable long userId, @RequestBody AlbumDto albumDto) {
    albumService.remove(userId, albumDto);
  }

  @PostMapping("/{id}/posts/add/{postId}")
  public void addPostToAlbum(@PathVariable long id, @PathVariable long postId,
      @RequestBody long userId) {
    albumService.addPost(id, postId, userId);
  }

  @DeleteMapping("/{id}/posts/remove/{postId}")
  public void removePostFromAlbum(@PathVariable long id, @PathVariable long postId,
      @RequestBody long userId) {
    albumService.removePost(id, postId, userId);
  }

  @PostMapping("/favorites/add/{id}")
  public void addAlbumToFavorites(@PathVariable long id, @RequestBody long userId) {
    albumService.addAlbumToFavorites(id, userId);
  }

  @DeleteMapping("/favorites/remove/{id}")
  public void removeAlbumToFavorites(@PathVariable long id, @RequestBody long userId) {
    albumService.removeAlbumFromFavorites(id, userId);
  }

  @GetMapping("/filters/user/{userId}")
  public List<AlbumDto> getUserAlbums(@PathVariable @Validated Long userId,
      @RequestBody AlbumFilterDto albumFilterDto) {
    return albumService.getUserAlbumsWithFilters(userId, albumFilterDto);
  }

  @GetMapping("/filters/favorites/{userId}")
  public List<AlbumDto> getUserFavoritesAlbums(@PathVariable @Validated Long userId,
      @RequestBody AlbumFilterDto albumFilterDto) {
    return albumService.getUserFavoriteAlbumsWithFilters(userId, albumFilterDto);
  }

  @GetMapping("/filters/all/{userId}")
  public List<AlbumDto> getAllAlbums(@PathVariable @Validated Long userId,
      @RequestBody AlbumFilterDto albumFilterDto) {
    return albumService.getAllAlbumsWithFilters(userId, albumFilterDto);
  }

}

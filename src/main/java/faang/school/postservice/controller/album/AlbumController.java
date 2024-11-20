package faang.school.postservice.controller.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.service.album.AlbumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

  @PostMapping("/{id}/posts/add/{postId}")
  public void addPostToAlbum(@PathVariable long id, @PathVariable long postId, @RequestBody long userId) {
    albumService.addPost(id, postId, userId);
  }

  @DeleteMapping("/{id}/posts/add/{postId}")
  public void removePostFromAlbum(@PathVariable long id, @PathVariable long postId, @RequestBody long userId) {
    albumService.removePost(id, postId, userId);
  }

}

package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDraftCreateDto;
import faang.school.postservice.dto.post.PostDraftResponseDto;
import faang.school.postservice.dto.post.PostPublishResponseDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.album.AlbumService;
import faang.school.postservice.service.resource.ResourceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
@Validated
public class PostService {
    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final UserServiceClient userService;
    private final ProjectServiceClient projectService;
    private final AlbumService albumService;
    private final ResourceService resourceService;

    @Transactional
    public PostDraftResponseDto createDraftPost(@Valid PostDraftCreateDto dto) {
        validateUserOrProject(dto.getAuthorId(), dto.getProjectId());
        Post postEntity = postMapper.toEntityFromDraftDto(dto);
        postEntity.setAlbums(albumService.getAlbumsByIds(dto.getAlbumsId()));
        postEntity.setResources(resourceService.getResourcesByIds(dto.getResourcesId()));
        return postMapper.toDraftDtoFromPost(postRepository.save(postEntity));
    }

    private void validateUserOrProject(Long userId, Long projectId) {
        if (userId != null) {
            existsUser(userId);
        }
        if (projectId != null) {
            existsProject(projectId);
        }
    }

    private void existsProject(Long projectId) {
        ProjectDto projectDto = projectService.getProject(projectId);
        if (projectDto == null) {
            throw new IllegalArgumentException("Project with id " + projectId + " not found");
        }
    }

    private void existsUser(Long userId) {
        UserDto userDto = userService.getUser(userId);
        if (userDto == null) {
            throw new IllegalArgumentException("User with id " + userId + " not found");
        }
    }

    public PostPublishResponseDto publishPost(@NotNull @Positive Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        validatePost(post, postId);

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        return postMapper.toPublishDtoFromPost(postRepository.save(post));
    }

    private void validatePost(Post post, Long id) {
        if (post == null) {
           throw new IllegalArgumentException("Post id:" + id + " not found");
        }
        if (post.getPublishedAt() != null) {
            throw new IllegalArgumentException("Post with id:" + id + " already published");
        }
    }
}





















































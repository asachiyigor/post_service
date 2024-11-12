package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDraftCreateDto;
import faang.school.postservice.dto.post.PostDraftResponseDto;
import faang.school.postservice.dto.post.PostPublishResponseDto;
import faang.school.postservice.dto.post.PostUpdateDto;
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
import java.util.List;


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
    public PostDraftResponseDto createDraftPost(@NotNull @Valid PostDraftCreateDto dto) {
        validateUserOrProject(dto.getAuthorId(), dto.getProjectId());
        Post postEntity = postMapper.toEntityFromDraftDto(dto);
        postEntity.setAlbums(albumService.getAlbumsByIds(dto.getAlbumsId()));
        postEntity.setResources(resourceService.getResourcesByIds(dto.getResourcesId()));
        return postMapper.toDraftDtoFromPost(postRepository.save(postEntity));
    }

    @Transactional
    public PostPublishResponseDto publishPost(@Positive long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        validatePost(post, postId);

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        return postMapper.toPublishDtoFromPost(postRepository.save(post));
    }

    @Transactional
    public PostPublishResponseDto updatePost(@Positive long postId, @NotNull @Valid PostUpdateDto dto) {
        Post post = postRepository.findById(postId).orElse(null);
        validatePost(post, postId);

        post.setContent(dto.getContent());
        return postMapper.toPublishDtoFromPost(postRepository.save(post));
    }

    @Transactional
    public void deletePostById(@Positive long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        validatePost(post, postId);
        post.setDeleted(true);
        postRepository.save(post);
    }

    public PostPublishResponseDto getPostById(@Positive long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        validatePost(post, postId);
        return postMapper.toPublishDtoFromPost(post);
    }

    private void validateUserOrProject(Long userId, Long projectId) {
        if (userId != null) {
            existsUser(userId);
        }
        if (projectId != null) {
            existsProject(projectId);
        }
    }

    private void existsProject(long projectId) {
        ProjectDto projectDto = projectService.getProject(projectId);
        if (projectDto == null) {
            throw new IllegalArgumentException("Project with id " + projectId + " not found");
        }
    }

    private void existsUser(long userId) {
        UserDto userDto = userService.getUser(userId);
        if (userDto == null) {
            throw new IllegalArgumentException("User with id " + userId + " not found");
        }
    }

    private void validatePost(Post post, long id) {
        if (post == null) {
            throw new IllegalArgumentException("Post id:" + id + " not found");
        }
        if (post.getPublishedAt() != null) {
            throw new IllegalArgumentException("Post with id:" + id + " already published");
        }
    }

    public List<PostDraftResponseDto> getAllDraftNonDelPostsByUserIdSortedCreatedAtDesc(long userId) {
        return postRepository.findByNotPublishedAndNotDeletedAndAuthorIdOrderCreatedAtDesc(userId).stream()
                .map(postMapper::toDraftDtoFromPost)
                .toList();
    }

    public List<PostDraftResponseDto> getAllDraftNonDelPostsByProjectIdSortedCreatedAtDesc(long projectId) {
        return postRepository.findByNotPublishedAndNotDeletedAndProjectIdOrderCreatedAtDesc(projectId).stream()
                .map(postMapper::toDraftDtoFromPost)
                .toList();
    }

    public List<PostPublishResponseDto> getAllPublishNonDelPostsByUserIdSortedCreatedAtDesc(long userId) {
        return postRepository.findByPublishedAndNotDeletedAndAuthorIdOrderCreatedAtDesc(userId).stream()
                .map(postMapper::toPublishDtoFromPost)
                .toList();
    }

    public List<PostPublishResponseDto> getAllPublishNonDelPostsByProjectIdSortedCreatedAtDesc(long projectId) {
        return postRepository.findByPublishedAndNotDeletedAndProjectIdOrderCreatedAtDesc(projectId).stream()
                .map(postMapper::toPublishDtoFromPost)
                .toList();
    }
}





















































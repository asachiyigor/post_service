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
import faang.school.postservice.validator.post.PostValidator;
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
    private final PostValidator postValidator;

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
        postValidator.validatePost(post, postId);

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        return postMapper.toPublishDtoFromPost(postRepository.save(post));
    }

    @Transactional
    public PostPublishResponseDto updatePost(@Positive long postId, @NotNull @Valid PostUpdateDto dto) {
        Post post = postRepository.findById(postId).orElse(null);
        postValidator.validatePost(post, postId);

        post.setContent(dto.getContent());
        return postMapper.toPublishDtoFromPost(postRepository.save(post));
    }

    @Transactional
    public void deletePostById(@Positive long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        postValidator.validatePost(post, postId);

        post.setDeleted(true);
        postRepository.save(post);
    }

    public PostPublishResponseDto getPostById(@Positive long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        postValidator.validatePost(post, postId);

        return postMapper.toPublishDtoFromPost(post);
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

    private void validateUserOrProject(Long userId, Long projectId) {
        ProjectDto projectDto;
        UserDto userDto;
        if (userId != null) {
            userDto = userService.getUser(userId);
            postValidator.validateDto(userDto);
        }
        if (projectId != null) {
            projectDto = projectService.getProject(projectId);
            postValidator.validateDto(projectDto);
        }
    }
}





















































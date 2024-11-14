package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDraftCreateDto;
import faang.school.postservice.dto.post.PostDraftResponseDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.album.AlbumService;
import faang.school.postservice.service.resource.ResourceService;
import faang.school.postservice.validator.dto.ProjectDtoValidator;
import faang.school.postservice.validator.dto.UserDtoValidator;
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
    private final UserDtoValidator userDtoValidator;
    private final ProjectDtoValidator projectDtoValidator;

    @Transactional
    public PostDraftResponseDto createDraftPost(@NotNull @Valid PostDraftCreateDto dto) {
        validateUserOrProject(dto.getAuthorId(), dto.getProjectId());

        Post postEntity = postMapper.toEntityFromDraftDto(dto);
        if (dto.getAlbumsId() != null){
            postEntity.setAlbums(albumService.getAlbumsByIds(dto.getAlbumsId()));
        }
        if (dto.getResourcesId() != null){
            postEntity.setResources(resourceService.getResourcesByIds(dto.getResourcesId()));
        }
        return postMapper.toDraftDtoFromPost(postRepository.save(postEntity));
    }

    @Transactional
    public PostResponseDto publishPost(@Positive long postId) {
        Post post = getPostById(postId);
        if (post.isPublished()) {
            throw new IllegalArgumentException("Post is already published");
        }
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        return postMapper.toDtoFromPost(postRepository.save(post));
    }

    @Transactional
    public PostResponseDto updatePost(@Positive long postId, @NotNull @Valid PostUpdateDto dto) {
        Post post = getPostById(postId);
        post.setContent(dto.getContent());
        return postMapper.toDtoFromPost(postRepository.save(post));
    }

    @Transactional
    public PostResponseDto deletePost(@Positive long postId) {
        Post post = getPostById(postId);
        post.setDeleted(true);
        return postMapper.toDtoFromPost(postRepository.save(post));
    }

    public PostResponseDto getPost(@Positive long postId) {
        Post post = getPostById(postId);
        return postMapper.toDtoFromPost(post);
    }

    public List<PostDraftResponseDto> getDraftPostsByUserIdSortedCreatedAtDesc(long userId) {
        return postRepository.findByNotPublishedAndNotDeletedAndAuthorIdOrderCreatedAtDesc(userId).stream()
                .map(postMapper::toDraftDtoFromPost)
                .toList();
    }

    public List<PostDraftResponseDto> getDraftPostsByProjectIdSortedCreatedAtDesc(long projectId) {
        return postRepository.findByNotPublishedAndNotDeletedAndProjectIdOrderCreatedAtDesc(projectId).stream()
                .map(postMapper::toDraftDtoFromPost)
                .toList();
    }

    public List<PostResponseDto> getPublishPostsByUserIdSortedCreatedAtDesc(long userId) {
        return postRepository.findByPublishedAndNotDeletedAndAuthorIdOrderCreatedAtDesc(userId).stream()
                .map(postMapper::toDtoFromPost)
                .toList();
    }

    public List<PostResponseDto> getPublishPostsByProjectIdSortedCreatedAtDesc(long projectId) {
        return postRepository.findByPublishedAndNotDeletedAndProjectIdOrderCreatedAtDesc(projectId).stream()
                .map(postMapper::toDtoFromPost)
                .toList();
    }

    private Post getPostById(@Positive long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));
    }

    private void validateUserOrProject(Long userId, Long projectId) {
        if (userId != null) {
            userDtoValidator.validateUserDto(userService.getUser(userId));
        }
        if (projectId != null) {
            projectDtoValidator.validateProjectDto(projectService.getProject(projectId));
        }
    }
}





















































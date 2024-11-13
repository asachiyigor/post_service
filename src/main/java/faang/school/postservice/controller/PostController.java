package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDraftCreateDto;
import faang.school.postservice.dto.post.PostDraftResponseDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.service.post.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/posts")
@Validated
public class PostController {
    private final PostService postService;

    @PostMapping("/draft")
    public PostDraftResponseDto createDraftPost(@RequestBody @Valid PostDraftCreateDto dto) {
        return postService.createDraftPost(dto);
    }

    @PutMapping("/{postId}/publish")
    public PostResponseDto publishPost(@PathVariable @Positive long postId) {
        return postService.publishPost(postId);
    }

    @PutMapping("/{postId}/update")
    public PostResponseDto updatePost(@PathVariable @Positive long postId, @RequestBody @Valid PostUpdateDto dto) {
        return postService.updatePost(postId, dto);
    }

    @DeleteMapping("/{postId}/delete")
    @ResponseStatus(HttpStatus.OK)
    public void deletePostById(@PathVariable @Positive long postId) {
        postService.deletePostById(postId);
    }

    @GetMapping("/{postId}")
    public PostResponseDto getPost(@PathVariable @Positive long postId) {
        return postService.getPost(postId);
    }

    @GetMapping("/user/{userId}/drafts")
    public List<PostDraftResponseDto> getAllDraftPostsByUserId(@PathVariable @Positive long userId) {
        return postService.getAllDraftNonDelPostsByUserIdSortedCreatedAtDesc(userId);
    }

    @GetMapping("/project/{projectId}/drafts")
    public List<PostDraftResponseDto> getAllDraftPostsByProjectId(@PathVariable @Positive long projectId) {
        return postService.getAllDraftNonDelPostsByProjectIdSortedCreatedAtDesc(projectId);
    }

    @GetMapping("/user/{userId}/publishes")
    public List<PostResponseDto> getAllPublishPostsByUserId(@PathVariable @Positive long userId) {
        return postService.getAllPublishNonDelPostsByUserIdSortedCreatedAtDesc(userId);
    }

    @GetMapping("/project/{projectId}/publishes")
    public List<PostResponseDto> getAllPublishPostsByProjectId(@PathVariable @Positive long projectId) {
        return postService.getAllPublishNonDelPostsByProjectIdSortedCreatedAtDesc(projectId);
    }
}

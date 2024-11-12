package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDraftCreateDto;
import faang.school.postservice.dto.post.PostDraftResponseDto;
import faang.school.postservice.dto.post.PostPublishResponseDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.service.post.PostService;
//import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.PostUpdate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/posts")
@Validated
public class PostController {
    private final PostService postService;

    //    @Operation(summary = "Create draft post")
    @PostMapping("/draft")
    public PostDraftResponseDto createDraftPost(@RequestBody @Valid PostDraftCreateDto dto) {
        return postService.createDraftPost(dto);
    }

    //    @Operation(summary = "Publish post")
    @PutMapping("/{postId}/publish")
    public PostPublishResponseDto publishPost(@PathVariable @Positive Long postId) {
        return postService.publishPost(postId);
    }

    //    @Operation(summary = "Update post")
    @PutMapping("/{postId}/update")
    public PostPublishResponseDto publishPost(@PathVariable @Positive Long postId,
                                              @RequestBody @Valid PostUpdateDto dto) {
        return postService.updatePost(postId, dto);
    }
}

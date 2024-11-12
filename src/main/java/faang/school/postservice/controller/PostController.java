package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDraftCreateDto;
import faang.school.postservice.dto.post.PostDraftResponseDto;
import faang.school.postservice.service.post.PostService;
//import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/posts")
@Validated
public class PostController {
    private final PostService postService;

    @PostMapping("/draft")
//    @Operation(summary = "Create draft post")
    public PostDraftResponseDto createDraftPost(@Valid @RequestBody PostDraftCreateDto dto) {
        return postService.createDraftPost(dto);
    }
}

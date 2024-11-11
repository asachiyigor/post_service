package faang.school.postservice.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDraftCreateDto {
    @Schema(description = "Post content", example = "Hello, world!")
    @NotBlank(message = "Content should not be blank")
    @Size(min = 1, max = 4096, message = "Content should be between 1 and 4096 characters")
    private String content;

    @Schema(description = "Post author ID", example = "1")
    @Positive(message = "Author ID must be a positive number or null")
    private Long authorId;

    @Schema(description = "Post project ID", example = "1")
    @Positive(message = "Project ID must be a positive number or null")
    private Long projectId;

    @Schema(description = "Post resources ID", example = "[1, 2, 3]")
    private List<@Positive Long> resourcesId;

    @Schema(description = "Post albums ID", example = "[1, 2, 3]")
    private List<@Positive Long> albumsId;

    @Schema(description = "The author of the post is checked")
    @AssertTrue(message = "The author of a post can be either a user or a project")
    public boolean isAuthorOrProject() {
        return authorId != null && projectId == null || authorId == null && projectId != null;
    }
}

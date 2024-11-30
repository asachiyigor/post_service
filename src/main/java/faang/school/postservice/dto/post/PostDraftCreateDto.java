package faang.school.postservice.dto.post;


import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDraftCreateDto {
    @NotBlank(message = "Content should not be blank")
    @Size(min = 1, max = 4096, message = "Content should be between 1 and 4096 characters")
    private String content;

    @Positive(message = "Author ID must be a positive number or null")
    private Long authorId;

    @Positive(message = "Project ID must be a positive number or null")
    private Long projectId;

    @Size(min = 1, message = "Resources ID should not be empty")
    private List<@NotNull @Positive Long> resourcesId;

    @Size(min = 1, message = "Albums ID should not be empty")
    private List<@NotNull @Positive Long> albumsId;


    private LocalDateTime scheduledAt;

    @AssertTrue(message = "The author of a post can be either a user or a project")
    public boolean isAuthorOrProject() {
        return authorId != null && projectId == null || authorId == null && projectId != null;
    }
}

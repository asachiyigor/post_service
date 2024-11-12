package faang.school.postservice.dto.post;

//import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostUpdateDto {
    //    @Schema(description = "Post content", example = "Hello, world!")
    @NotBlank(message = "Content should not be blank")
    @Size(min = 1, max = 4096, message = "Content should be between 1 and 4096 characters")
    private String content;
}

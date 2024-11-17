package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "Content should not be blank")
    @Size(min = 1, max = 4096, message = "Content should be between 1 and 4096 characters")
    private String content;
}

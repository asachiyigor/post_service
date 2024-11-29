package faang.school.postservice.dto.post;

import faang.school.postservice.model.Resource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostUpdateDto {
    @NotBlank(message = "Content should not be blank")
    @Size(min = 1, max = 4096, message = "Content should be between 1 and 4096 characters")
    private String content;
    private List<Long> resourcesIds;
    private LocalDateTime scheduledAt;
}

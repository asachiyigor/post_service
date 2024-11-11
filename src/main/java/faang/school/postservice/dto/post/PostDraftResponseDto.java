package faang.school.postservice.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDraftResponseDto {
    private Long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private List<Long> albumsIds;
    private List<Long> resourcesIds;
    private boolean published;
    private boolean deleted;
    private LocalDateTime createdAt;
}

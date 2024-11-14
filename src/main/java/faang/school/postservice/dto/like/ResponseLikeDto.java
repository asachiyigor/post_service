package faang.school.postservice.dto.like;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseLikeDto {
    @Positive
    private Long likeId;
    @Positive
    private Long userId;
    @Positive
    private Long postId;
    @Positive
    private Long commentId;
}

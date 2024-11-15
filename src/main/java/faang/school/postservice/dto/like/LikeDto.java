package faang.school.postservice.dto.like;

import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public abstract class LikeDto {
    @Positive
    private Long userId;
}

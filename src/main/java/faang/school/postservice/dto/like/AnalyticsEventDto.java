package faang.school.postservice.dto.like;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AnalyticsEventDto(
        @Positive
        Long receiverId,
        @NotNull
        Long actorId,
        LocalDateTime receivedAt
) {
}
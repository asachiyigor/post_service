package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentDto {

    @NotBlank
    @Size(min = 1, max = 4036)
    String content;

    long id;
    long authorId;
    long postId;
}

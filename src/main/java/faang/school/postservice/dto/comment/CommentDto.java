package faang.school.postservice.dto.comment;

import faang.school.postservice.model.Like;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class CommentDto {
    private long id;

    @NotBlank
    @Size(min = 1, max = 4036)
    private String content;

    long authorId;
    private List<Like> likes;
    private long postId;
}

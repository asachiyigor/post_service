package faang.school.postservice.dto.comment;

import faang.school.postservice.model.Like;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class ResponseCommentDto {
    @NotNull(message = "Id комментария не должен быть null")
    @Positive(message = "Id комментария должен быть положительным")
    private Long id;

    @NotBlank(message = "Комментарий не может быть пустым")
    @Size(min = 1, max = 4036, message = "Комментарий должен быть не меньше 1 символа и не больше 4036")
    private String content;

    @NotNull(message = "Id автора комментария не должен быть null")
    @Positive(message = "Id автора комментария должен быть положительным")
    private Long authorId;

    @NotNull(message = "Список комментария не должен быть null")
    private List<@NotNull @Min(1) Like> likes;

    @NotNull(message = "Id поста комментария не должен быть null")
    @Positive(message = "Id поста комментария должен быть положительным")
    private Long postId;
}

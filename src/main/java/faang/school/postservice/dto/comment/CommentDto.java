package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentDto {
    long id;

    @NotBlank
    @Size(min = 1, max = 4036, message = "Комментарий должен быть от 1 до 4036 символов")
    String content;

    @Positive(message = "id автора должен быть больше ноля")
    long authorId;

    @Positive(message = "Id поста должен быть больше ноля")
    long postId;
}

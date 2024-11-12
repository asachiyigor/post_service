package faang.school.postservice.dto.project;

import faang.school.postservice.dto.OutSideDto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDto extends OutSideDto {
    @Min(value = 1, message = "ID must be a positive number")
    private long id;
    @NotBlank(message = "Title should not be blank")
    private String title;
}

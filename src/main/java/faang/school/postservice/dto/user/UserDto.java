package faang.school.postservice.dto.user;

import faang.school.postservice.dto.OutSideDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto extends OutSideDto {
    @Min(value = 1, message = "ID must be a positive number")
    private Long id;
    @NotBlank(message = "Title should not be blank")
    private String username;
    @Email(message = "Email must be in right format")
    private String email;
}

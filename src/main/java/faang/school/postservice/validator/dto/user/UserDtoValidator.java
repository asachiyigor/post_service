package faang.school.postservice.validator.dto.user;

import faang.school.postservice.dto.user.UserDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserDtoValidator {
    public void validateUserDto(UserDto userDto) {
        if (userDto == null) {
            throw new EntityNotFoundException("User not found");
        }
    }
}

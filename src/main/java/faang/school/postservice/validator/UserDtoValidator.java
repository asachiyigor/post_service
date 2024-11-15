package faang.school.postservice.validator;

import faang.school.postservice.dto.user.UserDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserDtoValidator {
    public void isUserDto(UserDto userDto) {
        if (userDto == null) {
            throw new EntityNotFoundException("User not found");
        }
    }
}

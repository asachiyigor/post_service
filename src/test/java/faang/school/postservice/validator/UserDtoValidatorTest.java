package faang.school.postservice.validator;

import faang.school.postservice.dto.user.UserDto;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserDtoValidatorTest {

    @InjectMocks
    private UserDtoValidator userDtoValidator;

    @Test
    public void negativeTestUserDtoValidator() {
        UserDto userDto = null;
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> userDtoValidator.isUserDto(userDto));
    }

    @Test
    public void positiveTestUserDtoValidator() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .build();
        Assertions.assertDoesNotThrow(() -> userDtoValidator.isUserDto(userDto));
    }
}

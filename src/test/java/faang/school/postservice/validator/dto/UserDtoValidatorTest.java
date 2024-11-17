package faang.school.postservice.validator.dto;

import faang.school.postservice.dto.user.UserDto;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserDtoValidatorTest {
    @InjectMocks
    private UserDtoValidator userDtoValidator;

    @Test
    void testValidateUserDto_Negative() {
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> userDtoValidator.validateUserDto(null));
    }

    @Test
    void testValidateUserDto_Positive() {
        UserDto userDto = UserDto.builder().id(1L).build();

        Assertions.assertDoesNotThrow(() -> userDtoValidator.validateUserDto(userDto));
    }
}
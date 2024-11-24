package faang.school.postservice.validator.dto.project;

import faang.school.postservice.dto.project.ProjectDto;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectDtoValidatorTest {
    @InjectMocks
    private ProjectDtoValidator projectDtoValidator;

    @Test
    void testValidateProjectDto_Negative() {
        Assertions.assertThrows(EntityNotFoundException.class,
                ()-> projectDtoValidator.validateProjectDto(null));
    }

    @Test
    void testValidateProjectDto_Positive() {
        ProjectDto projectDto = ProjectDto.builder().id(1L).build();

        Assertions.assertDoesNotThrow(()-> projectDtoValidator.validateProjectDto(projectDto));
    }
}
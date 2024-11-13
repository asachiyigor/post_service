package faang.school.postservice.validator.dto;

import faang.school.postservice.dto.project.ProjectDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class ProjectDtoValidator {
    public void validateProjectDto(ProjectDto projectDto) {
        if (projectDto == null) {
            throw new EntityNotFoundException("Project not found");
        }
    }
}

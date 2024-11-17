package faang.school.postservice.service.resource;

import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.ResourceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ResourceService {
    private final ResourceRepository resourceRepository;

    public List<Resource> getResourcesByIds(List<Long> ids) {
        if (ids == null){
            return null;
        }
        return resourceRepository.findAllById(ids);
    }
}

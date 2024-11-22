package faang.school.postservice.service.resource;

import faang.school.postservice.model.Resource;

import java.util.List;

public interface ResourceService {
    Resource save(Resource resource);

    Resource getResource(Long id);

    List<Resource> getResourcesByIds(List<Long> ids);
}
package faang.school.postservice.validator.post;

import faang.school.postservice.dto.OutSideDto;
import faang.school.postservice.model.Post;
import org.springframework.stereotype.Component;

@Component
public class PostValidator {
    public void validateDto(OutSideDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("User or project not found");
        }
    }

    public void validatePost(Post post, long id) {
        if (post == null) {
            throw new IllegalArgumentException("Post id:" + id + " not found");
        }
        if (post.getPublishedAt() != null) {
            throw new IllegalArgumentException("Post with id:" + id + " already published");
        }
    }
}

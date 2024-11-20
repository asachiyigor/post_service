package faang.school.postservice.validator.post;

import org.springframework.stereotype.Component;

@Component
public class PostIdValidator {
    public void postIdValidate(Long postId) {
        if (postId == null) {
            throw new IllegalArgumentException("Invalid post ID");
        }
    }
}
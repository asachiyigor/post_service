package faang.school.postservice.service.post;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
    }

    public boolean existsPost(Long postId) {
        return postRepository.existsById(postId);
    }
}

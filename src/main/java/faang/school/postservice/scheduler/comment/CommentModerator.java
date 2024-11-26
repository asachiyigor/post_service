package faang.school.postservice.scheduler.comment;

import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentModerator {

    private final CommentRepository commentRepository;

    @Scheduled(cron = "${comment.moderator.scheduler.cron}")
    public void verifyComments() {
        List<Comment> comments = commentRepository.findAllUnCheckedComments();
        if (comments.isEmpty()){
            log.info("No comments to verify");
        }

    }
}

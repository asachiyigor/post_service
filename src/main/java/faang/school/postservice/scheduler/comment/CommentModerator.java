package faang.school.postservice.scheduler.comment;

import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentModerator {
    private final CommentRepository commentRepository;
    private final CommentService commentService;

    @Value("${comment.moderator.subList-size}")
    private Integer subListSize;

    @Scheduled(fixedRateString = "${comment.moderator.scheduler.milliseconds}")
    public void moderateComments() {
        List<Comment> comments = commentRepository.findAllUnCheckedComments();
        if (comments.isEmpty()){
            log.info("No comments for moderation");
            return;
        }
        List<List<Comment>> subLists = ListUtils.partition(comments, subListSize);
        subLists.forEach(commentService::verifyComments);
    }
}

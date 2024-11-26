package faang.school.postservice.config.moderation;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.Set;

@Component
public class ModerationDictionary {

    @Getter
    private Set<String> curseWords;

    @Value("${comment.moderator.path-curse-words}")
    private Path curseWordsPath;
}

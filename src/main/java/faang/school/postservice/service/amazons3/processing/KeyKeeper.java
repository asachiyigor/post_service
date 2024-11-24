package faang.school.postservice.service.amazons3.processing;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class KeyKeeper {
    public String getKeyFile(String folder) {
        return String.format("%s/%s", folder, UUID.randomUUID());
    }
}

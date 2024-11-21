package faang.school.postservice.validator.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileValidation {
    @Value("${file.max-size-image}")
    private static long MAX_IMAGE_SIZE; // 5 MB
    private static final long MAX_VIDEO_SIZE = 50 * 1024 * 1024; // 50 MB
    private static final long MAX_AUDIO_SIZE = 10 * 1024 * 1024; // 10 MB

    public boolean checkFiles(MultipartFile[] files) {
        for (MultipartFile file : files) {
            String contentType = file.getContentType();
            long fileSize = file.getSize();

            if (contentType != null) {
                if (isImage(contentType) && fileSize > MAX_IMAGE_SIZE) {
                    return false;
                } else if (isVideo(contentType) && fileSize > MAX_VIDEO_SIZE) {
                    return false;
                } else if (isAudio(contentType) && fileSize > MAX_AUDIO_SIZE) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isImage(String contentType) {
        return contentType.startsWith("image/");
    }

    private boolean isVideo(String contentType) {
        return contentType.startsWith("video/");
    }

    private boolean isAudio(String contentType) {
        return contentType.startsWith("audio/");
    }
}

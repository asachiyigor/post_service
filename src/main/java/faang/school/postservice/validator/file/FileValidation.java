package faang.school.postservice.validator.file;

import faang.school.postservice.exception.ExceptionMessage;
import faang.school.postservice.exception.FileException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
public class FileValidation {

    @Value("${file.max-size-image}")
    private static long maxImageSize; // 5 MB

    @Value("${file.max-size-video}")
    private static long maxVideoSize; // 50 MB

    @Value("${file.max-size-audio}")
    private static long maxAudioSize; // 10 MB

    @Value("${file.max-count-files}")
    private int maxCountFiles; // 10

    public boolean contentIsImage(MultipartFile file) {
        String contentType = file.getContentType();
        assert contentType != null;
        return isImage(contentType);
    }

    public void checkingTotalOfFiles(int numberFiles, int numberResourcesInPost) {
        if ((numberFiles + numberResourcesInPost) > maxImageSize) {
            log.error(ExceptionMessage.INCORRECT_NUMBER_OF_FILES.getMessage());
            throw new FileException(ExceptionMessage.INCORRECT_NUMBER_OF_FILES.getMessage());
        }
    }

    public void checkFiles(MultipartFile[] files) {
        if (files.length > maxCountFiles && files[0] != null) {
            log.error(ExceptionMessage.INCORRECT_NUMBER_OF_FILES.getMessage());
            throw new FileException(ExceptionMessage.FILE_EXCEPTION.getMessage());
        }
        for (MultipartFile file : files) {
            String contentType = file.getContentType();
            long fileSize = file.getSize();

            if (contentType != null) {
                if (isImage(contentType) && fileSize > maxImageSize) {
                    log.error("Image. {}", ExceptionMessage.FILE_TOO_LARGE.getMessage());
                    throw new FileException(ExceptionMessage.FILE_TOO_LARGE.getMessage());
                } else if (isVideo(contentType) && fileSize > maxVideoSize) {
                    log.error("Video. {}", ExceptionMessage.FILE_TOO_LARGE.getMessage());
                    throw new FileException(ExceptionMessage.FILE_TOO_LARGE.getMessage());
                } else if (isAudio(contentType) && fileSize > maxAudioSize) {
                    log.error("Audio. {}", ExceptionMessage.FILE_TOO_LARGE.getMessage());
                    throw new FileException(ExceptionMessage.FILE_TOO_LARGE.getMessage());
                }
            } else {
                log.error(ExceptionMessage.FILE_IS_EMPTY.getMessage());
                throw new FileException(ExceptionMessage.FILE_IS_EMPTY.getMessage());
            }
        }
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

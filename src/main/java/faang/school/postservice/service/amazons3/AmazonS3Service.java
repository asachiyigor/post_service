package faang.school.postservice.service.amazons3;

import faang.school.postservice.model.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AmazonS3Service {
    Resource uploadFile(MultipartFile file, String folder) throws IOException;

    Resource getFile(String fileId);

    Resource deleteFile(String bucket, String key);
}

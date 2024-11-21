package faang.school.postservice.service.amazons3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.postservice.exception.ExceptionMessage;
import faang.school.postservice.exception.FileException;
import faang.school.postservice.model.Resource;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@ConditionalOnProperty(value = "services.s3.isMocked", havingValue = "false")
public class Amazons3ServiceImpl implements AmazonS3Service {
    private final AmazonS3 s3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Override
    public Resource uploadFile(MultipartFile file, String folder) throws IOException {
        if (!s3Client.doesBucketExistV2(bucketName)) {
            s3Client.createBucket(bucketName);
        }
        InputStream inputStream = file.getInputStream();

        //надо разобраться как поменять размер картинок и как вычислять размер картинок
        //сделать эту валидацию в валидаторе

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());
        String key = String.format("%s/%s", UUID.randomUUID(), file.getOriginalFilename());
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, inputStream, objectMetadata);
            s3Client.putObject(putObjectRequest);
        } catch (Exception e) {
            log.error("when trying to add a file an error occurred {}", e.getMessage());
            throw new FileException(ExceptionMessage.FILE_EXCEPTION.getMessage());
        }
        return Resource.builder()
                .name(file.getOriginalFilename())
                .key(key)
                .size(file.getSize())
                .type(file.getContentType())
                .build();
    }

    @Override
    public Resource getFile(String fileId) {
        return null;
    }

    @Override
    public Resource deleteFile(String folder, String key) {
        return null;
    }
}

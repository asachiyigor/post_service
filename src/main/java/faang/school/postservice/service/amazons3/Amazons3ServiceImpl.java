package faang.school.postservice.service.amazons3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.postservice.exception.ExceptionMessage;
import faang.school.postservice.exception.FileException;
import faang.school.postservice.service.amazons3.processing.ImageProcessingService;
import faang.school.postservice.validator.file.FileValidation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@ConditionalOnProperty(value = "services.s3.isMocked", havingValue = "false")
public class Amazons3ServiceImpl implements AmazonS3Service {
    private final AmazonS3 s3Client;
    private final FileValidation fileValidation;
    private final ImageProcessingService imageProcessingService;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Override
    public void uploadFile(MultipartFile file, String key) throws IOException {
        if (!s3Client.doesBucketExistV2(bucketName)) {
            s3Client.createBucket(bucketName);
        }

        InputStream inputStream = file.getInputStream();
        if (fileValidation.contentIsImage(file)) {
            inputStream = imageProcessingService.optimizeImage(inputStream);
        }

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, inputStream, objectMetadata);
            s3Client.putObject(putObjectRequest);
        } catch (Exception e) {
            log.error("when trying to add a file an error occurred {}", e.getMessage());
            throw new FileException(ExceptionMessage.FILE_EXCEPTION.getMessage());
        }
    }

    @Override
    public InputStream getFile(String fileId) {
        S3Object answer = s3Client.getObject(bucketName, fileId);
        return answer.getObjectContent();
    }

    @Override
    public void deleteFile(String key) {
        s3Client.deleteObject(bucketName, key);
    }
}

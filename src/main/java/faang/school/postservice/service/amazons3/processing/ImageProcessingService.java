package faang.school.postservice.service.amazons3.processing;

import faang.school.postservice.exception.ExceptionMessage;
import faang.school.postservice.exception.FileException;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

@Slf4j
@Component
public class ImageProcessingService {

    @Value("${file.max_vertical_width}")
    private static int maxVerticalWidth;

    @Value("${file.max_vertical_height}")
    private static int maxVerticalHeight;

    @Value("${file.max_horizontal_width}")
    private static int maxHorizontalWidth;

    @Value("${file.max_horizontal_height}")
    private static int maxHorizontalHeight;

    public InputStream optimizeImage(InputStream inputStream) throws IOException {
        BufferedImage originalImage = ImageIO.read(inputStream);
        if (originalImage == null) {
            log.error(ExceptionMessage.FILE_IS_EMPTY.getMessage());
            throw new FileException(ExceptionMessage.FILE_IS_EMPTY.getMessage());
        }

        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        if (isVertical(width, height)) {
            if (width > maxVerticalWidth || height > maxVerticalHeight) {
                originalImage = resizeImage(originalImage, maxVerticalWidth, maxVerticalHeight);
            }
        } else {
            if (width > maxHorizontalWidth || height > maxHorizontalHeight) {
                originalImage = resizeImage(originalImage, maxHorizontalWidth, maxHorizontalHeight);
            }
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(originalImage, "jpg", outputStream);
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    private boolean isVertical(int width, int height) {
        return width < height;
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int newWidth, int newHeight) throws IOException {
        return Thumbnails.of(originalImage)
                .size(newWidth, newHeight)
                .keepAspectRatio(true)
                .asBufferedImage();
    }
}

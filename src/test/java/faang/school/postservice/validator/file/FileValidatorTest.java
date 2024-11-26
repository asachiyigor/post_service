package faang.school.postservice.validator.file;

import faang.school.postservice.exception.FileException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class FileValidatorTest {
    @InjectMocks
    private FileValidator fileValidator;

    @Mock
    private MockMultipartFile mockImageFile;

    @Mock
    private MockMultipartFile mockVideoFile;

    @Mock
    private MockMultipartFile mockAudioFile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        fileValidator = new FileValidator();

        fileValidator.setMaxImageSize(5 * 1024 * 1024L); // 5 MB
        fileValidator.setMaxVideoSize(50 * 1024 * 1024L); // 50 MB
        fileValidator.setMaxAudioSize(10 * 1024 * 1024L); // 10 MB
        fileValidator.setMaxCountFiles(10); // 10 файлов
    }

    @Test
    void testContentIsImage_WhenImageFile_ReturnsTrue() {
        when(mockImageFile.getContentType()).thenReturn("image/png");

        boolean result = fileValidator.contentIsImage(mockImageFile);

        assertTrue(result);
    }

    @Test
    void testContentIsImage_WhenNotImageFile_ReturnsFalse() {
        when(mockImageFile.getContentType()).thenReturn("video/mp4");

        boolean result = fileValidator.contentIsImage(mockImageFile);

        assertFalse(result);
    }

    @Test
    void testCheckFiles_WhenFileSizeExceeds_ThrowsException() {
        when(mockImageFile.getContentType()).thenReturn("image/png");
        when(mockImageFile.getSize()).thenReturn(6 * 1024 * 1024L); // 6 MB

        Exception exception = assertThrows(FileException.class, () -> {
            fileValidator.checkFiles(new MockMultipartFile[]{mockImageFile});
        });

        assertEquals("File too large", exception.getMessage());
    }

    @Test
    void testCheckFiles_WhenTooManyFiles_ThrowsException() {
        MockMultipartFile[] files = new MockMultipartFile[fileValidator.getMaxCountFiles() + 1];
        for (int i = 0; i < files.length; i++) {
            files[i] = new MockMultipartFile("file" + i, "file" + i + ".png", "image/png", new byte[0]);
        }

        Exception exception = assertThrows(FileException.class, () -> {
            fileValidator.checkFiles(files);
        });

        assertEquals("Error processing file", exception.getMessage());
    }

    @Test
    void testCheckFiles_WhenValidFiles_DoesNotThrow() {
        when(mockImageFile.getContentType()).thenReturn("image/png");
        when(mockImageFile.getSize()).thenReturn(4 * 1024 * 1024L); // 4 MB

        assertDoesNotThrow(() -> fileValidator.checkFiles(new MockMultipartFile[]{mockImageFile}));
    }
}

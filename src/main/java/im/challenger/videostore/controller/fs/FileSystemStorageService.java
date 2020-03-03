package im.challenger.videostore.controller.fs;

import com.google.common.io.Files;
import lombok.NonNull;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

public class FileSystemStorageService {

    public static final String FILE_STORAGE_PATH = "/opt/challenger/videos";

    public static ResourceRegion resourceRegion(UrlResource video, HttpHeaders headers) throws IOException {
        long contentLength = video.contentLength();
        List<HttpRange> ranges = headers.getRange();
        HttpRange range = ranges.stream().findFirst().orElse(null);
        if (range != null) {
            long start = range.getRangeStart(contentLength);
            long end = range.getRangeEnd(contentLength);
            long rangeLength = end - start + 1;
            return new ResourceRegion(video, start, rangeLength);
        } else {
            return new ResourceRegion(video, 0, contentLength);
        }
    }

    public static String upload(MultipartFile multipartFile) throws IOException {
        String newFileName = getNewFileName(multipartFile);
        Path filepath = Paths.get(FILE_STORAGE_PATH, newFileName);
        byte[] bytes = multipartFile.getBytes();
        Files.write(bytes, filepath.toFile());
        return newFileName;
    }

    private static String getNewFileName(@NonNull MultipartFile multipartFile) {
        String extension = Files.getFileExtension(multipartFile.getOriginalFilename());
        String randomNamePart = UUID.randomUUID().toString();
        return extension.isEmpty() ? randomNamePart : randomNamePart + "." + extension;
    }

}

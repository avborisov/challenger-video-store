package im.challenger.videostore.controller;

import com.microsoft.azure.storage.StorageException;
import im.challenger.videostore.controller.fs.FileSystemStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.MultipartConfigElement;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

@RestController
@Slf4j
public class VideoStoreController {

    @PostMapping(value = "/files/upload")
    public ResponseEntity uploadFile(@RequestParam("file") MultipartFile file, @RequestHeader Map<String, String> headers) {
        try {
            return ResponseEntity.ok(FileSystemStorageService.upload(file));
        } catch (IOException e) {
            log.error("Can't upload file", e);
        }
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Can't upload file, internal server error");
    }

    @GetMapping("/files/get/{filename}")
    public ResponseEntity<ResourceRegion> downloadFile(@PathVariable String filename, @RequestHeader HttpHeaders headers) {
        try {
            UrlResource video = new UrlResource(String.format("file:%s/%s", FileSystemStorageService.FILE_STORAGE_PATH, filename));
            ResourceRegion region = FileSystemStorageService.resourceRegion(video, headers);
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .contentType(MediaTypeFactory
                            .getMediaType(video)
                            .orElse(MediaType.APPLICATION_OCTET_STREAM))
                    .body(region);
        } catch (IOException e) {
            log.error("Can't get file", e);
            return null;
        }
    }

}

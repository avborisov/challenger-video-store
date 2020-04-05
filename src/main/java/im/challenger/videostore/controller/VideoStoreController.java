package im.challenger.videostore.controller;

import im.challenger.videostore.controller.fs.FileSystemStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class VideoStoreController {

    @PostMapping(value = "/files/upload")

    public ResponseEntity uploadFile(@RequestParam("file") MultipartFile file, @RequestHeader HttpHeaders headers) {
        try {
            String auth = headers.getFirst(HttpHeaders.AUTHORIZATION);
            log.info("Try to upload file with original name: {}\n\nheaders: \n{}", file.getOriginalFilename(), headers.toString());
            if (auth == null) {
                log.info("Unauthorized access error");
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("Unauthorized access error");
            } else {
                // TODO: check token
            }

            String uploadedFileName = FileSystemStorageService.upload(file);
            log.info("File uploaded successfully, new file name: {}", uploadedFileName);
            return ResponseEntity.ok(uploadedFileName);
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
            log.info("Try to download file with name: {}\n\nheaders: \n{}", filename, headers.toString());
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

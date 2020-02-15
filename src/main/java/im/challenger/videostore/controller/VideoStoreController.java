package im.challenger.videostore.controller;

import com.microsoft.azure.storage.StorageException;
import im.challenger.videostore.controller.azure.BlobStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

@RestController
@Slf4j
public class VideoStoreController {

    @Autowired
    BlobStorageService blobService;

    @PostMapping(value = "/files/upload")
    public ResponseEntity uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.ok(blobService.upload(file));
        } catch (StorageException | URISyntaxException | IOException e) {
            log.error("Can't upload file", e);
        }
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Can't upload file, internal server error");
    }

    @GetMapping("/files/get/{filename}")
    public ResponseEntity downloadFile(@PathVariable String filename) {
        try {
            URI blobUri = blobService.getBlobUri(filename);
            UrlResource video = new UrlResource(blobUri);
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .contentType(MediaTypeFactory
                            .getMediaType(video)
                            .orElse(MediaType.APPLICATION_OCTET_STREAM))
                    .body(video);
        } catch (URISyntaxException | StorageException | MalformedURLException e) {
            log.error("Can't upload file", e);
        }
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Can't download file, internal server error");

    }

}

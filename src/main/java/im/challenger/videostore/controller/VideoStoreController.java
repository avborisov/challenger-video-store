package im.challenger.videostore.controller;

import im.challenger.videostore.controller.fs.IStorageService;
import im.challenger.videostore.core.AuthApiTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class VideoStoreController {

    @Autowired
    private IStorageService storageService;

    @PostMapping(value = "/files/upload")
    public ResponseEntity uploadFile(@RequestParam("file") MultipartFile file, @RequestHeader HttpHeaders headers) {
        try {
            String auth = headers.getFirst(HttpHeaders.AUTHORIZATION);
            log.info("Try to upload file with original name: {}\nheaders: {}\n", file.getOriginalFilename(), headers.toString());

            if (auth == null || !AuthApiTools.isAuthTokenValid(auth)) {
                log.warn("Unauthorized access error");
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("Unauthorized access error");
            }

            String uploadedFileName = storageService.upload(file);
            log.info("File uploaded successfully, new file name: {}", uploadedFileName);
            return ResponseEntity.ok(uploadedFileName);
        } catch (Exception e) {
            log.error("Can't upload file", e);
        }
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Can't upload file, internal server error");
    }

    @GetMapping("/files/get/{filename}")
    public ResponseEntity<ResourceRegion> downloadFile(@PathVariable String filename, @RequestHeader HttpHeaders headers) {
        try {
            log.info("Try to download file with name: {}\nheaders: {}\n", filename, headers.toString());
            ResourceRegion region = storageService.download(filename, headers);
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .contentType(MediaTypeFactory
                            .getMediaType(filename)
                            .orElse(MediaType.APPLICATION_OCTET_STREAM))
                    .body(region);
        } catch (Exception e) {
            log.error("Can't get file", e);
            return null;
        }
    }

}

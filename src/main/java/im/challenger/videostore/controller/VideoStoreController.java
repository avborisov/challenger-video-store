package im.challenger.videostore.controller;

import im.challenger.videostore.controller.fs.IStorageService;
import im.challenger.videostore.core.AuthApiTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

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
            log.error("Try to upload file with original name: {}\nheaders: {}\n", file.getOriginalFilename(), headers.toString());
            log.error("Can't upload file", e);
        }
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Can't upload file, internal server error");
    }

    @GetMapping("/files/get/{filename}")
    public ResponseEntity downloadFile(@PathVariable String filename, @RequestHeader HttpHeaders headers) {
        try {
            ResourceRegion region = storageService.download(filename, headers);
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .contentType(MediaTypeFactory
                            .getMediaType(filename)
                            .orElse(MediaType.APPLICATION_OCTET_STREAM))
                    .body(region);
        } catch (UnsupportedOperationException e) {
            log.error("Try to download file by name {}, but current storage {} not support this operation in this manner", filename, storageService.getClass().getSimpleName());
            return ResponseEntity
                    .status(HttpStatus.NOT_IMPLEMENTED)
                    .body(e.getMessage());
        } catch (Exception e) {
            log.error("Try to download file with name: {}\nheaders: {}\n", filename, headers.toString(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

}

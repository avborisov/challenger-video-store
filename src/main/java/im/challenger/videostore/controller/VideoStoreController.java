package im.challenger.videostore.controller;

import im.challenger.videostore.controller.fs.FileSystemStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class VideoStoreController {

    @PostMapping(value = "/files/upload")

    public ResponseEntity uploadFile(@RequestParam("file") MultipartFile file, @RequestHeader HttpHeaders headers) {
        try {
            String auth = headers.getFirst(HttpHeaders.AUTHORIZATION);
            log.info("Try to upload file with original name: {}\nheaders: {}\n", file.getOriginalFilename(), headers.toString());

            if (auth == null || !isAuthTokenValid(auth)) {
                log.warn("Unauthorized access error");
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("Unauthorized access error");
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
            log.info("Try to download file with name: {}\nheaders: {}\n", filename, headers.toString());
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

    private boolean isAuthTokenValid(String auth) throws IOException {
        URL url = new URL("https://challenger.im/backend/user/isTokenValid");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty (HttpHeaders.AUTHORIZATION, auth);
        con.setRequestMethod("GET");
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);
        int responseCode = con.getResponseCode();
        log.info("isTokenValid token response code: {}", responseCode);
        con.disconnect();
        return HttpStatus.OK.value() == responseCode;
    }

}

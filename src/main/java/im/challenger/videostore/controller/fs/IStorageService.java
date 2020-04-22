package im.challenger.videostore.controller.fs;

import lombok.NonNull;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface IStorageService {

    ResourceRegion download(String filename, HttpHeaders headers) throws Exception;
    String upload(MultipartFile multipartFile) throws Exception;

    default String getNewFileName(@NonNull MultipartFile multipartFile) {
        String extension = StringUtils.getFilenameExtension(multipartFile.getOriginalFilename());
        String randomNamePart = UUID.randomUUID().toString();
        return extension.isEmpty() ? randomNamePart : randomNamePart + "." + extension;
    }
}

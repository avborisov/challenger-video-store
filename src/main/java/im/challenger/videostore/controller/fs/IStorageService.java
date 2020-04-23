package im.challenger.videostore.controller.fs;

import lombok.NonNull;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.net.URL;
import java.util.UUID;

public interface IStorageService {

    /**
     * @param filename name of file for downloading from storage
     * @param headers HTTP headers for RANGES
     * @return ResourceRegion with part all whole file
     * @throws Exception
     */
    ResourceRegion download(String filename, HttpHeaders headers) throws Exception;

    /**
     * @param multipartFile file to upload on storage
     * @return String representation of URL for downloading uploaded file
     * @throws Exception
     */
    String upload(MultipartFile multipartFile) throws Exception;

    default String getNewFileName(@NonNull MultipartFile multipartFile) {
        String extension = StringUtils.getFilenameExtension(multipartFile.getOriginalFilename());
        String randomNamePart = UUID.randomUUID().toString();
        return extension.isEmpty() ? randomNamePart : randomNamePart + "." + extension;
    }
}

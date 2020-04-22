package im.challenger.videostore.controller.fs;

import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.web.multipart.MultipartFile;

public interface IStorageService {

    ResourceRegion download(String filename, HttpHeaders headers) throws Exception;
    String upload(MultipartFile multipartFile) throws Exception;

}

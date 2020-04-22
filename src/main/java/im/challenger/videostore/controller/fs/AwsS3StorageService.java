package im.challenger.videostore.controller.fs;

import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.web.multipart.MultipartFile;

public class AwsS3StorageService implements IStorageService {

    @Override
    public ResourceRegion download(String filename, HttpHeaders headers) throws Exception {
        return null;
    }

    @Override
    public String upload(MultipartFile multipartFile) throws Exception {
        return null;
    }
}

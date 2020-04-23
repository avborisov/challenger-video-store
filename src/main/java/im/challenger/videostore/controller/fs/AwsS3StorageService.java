package im.challenger.videostore.controller.fs;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.OperationNotSupportedException;
import javax.servlet.http.HttpServletRequest;
import java.net.URL;

@Slf4j
public class AwsS3StorageService implements IStorageService {

    @Autowired
    ResourceLoader resourceLoader;
    @Autowired
    private AmazonS3 amazonS3Client;
    @Value("${aws.bucket.name}")
    private String bucketName;

    @Override
    public ResourceRegion download(String filename, HttpHeaders headers) throws Exception {
        throw new OperationNotSupportedException("Download directly from S3 service");
    }

    @Override
    public String upload(MultipartFile multipartFile) throws Exception {
        String newFileName = getNewFileName(multipartFile);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(multipartFile.getContentType());
        metadata.setContentLength(multipartFile.getSize());
        PutObjectRequest putObjectRequest = new PutObjectRequest(
                bucketName,
                newFileName,
                multipartFile.getInputStream(),
                metadata).withCannedAcl(CannedAccessControlList.PublicRead);
        amazonS3Client.putObject(putObjectRequest);
        URL uploadedFileURL = amazonS3Client.getUrl(bucketName, newFileName);
        return uploadedFileURL.toString();
    }
}

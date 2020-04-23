package im.challenger.videostore.controller.fs;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
public class AwsS3StorageService implements IStorageService {

    @Autowired
    private AmazonS3 amazonS3Client;

    @Autowired
    ResourceLoader resourceLoader;

    @Value("${aws.bucket.name}")
    private String bucketName;

    @Override
    public ResourceRegion download(String filename, HttpHeaders headers) throws Exception {
        S3Object fullContent = amazonS3Client.getObject(bucketName, filename);
        long contentLength = fullContent.getObjectMetadata().getContentLength();
        List<HttpRange> ranges = headers.getRange();
        HttpRange range = ranges.stream().findFirst().orElse(null);

        byte[] content = StreamUtils.copyToByteArray(fullContent.getObjectContent());
        if (range == null) {
            return new ResourceRegion(new ByteArrayResource(content), 0, contentLength);
        }

        long start = range.getRangeStart(contentLength);
        long end = range.getRangeEnd(contentLength);
        long rangeLength = end - start + 1;

        return new ResourceRegion(new ByteArrayResource(content), start, rangeLength);
    }

    @Override
    public String upload(MultipartFile multipartFile) throws Exception {
        String newFileName = getNewFileName(multipartFile);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(multipartFile.getContentType());
        metadata.setContentLength(multipartFile.getSize());
        amazonS3Client.putObject(
                bucketName,
                newFileName,
                multipartFile.getInputStream(),
                metadata
        );
        return newFileName;
    }
}

package im.challenger.videostore;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import im.challenger.videostore.controller.fs.AwsS3StorageService;
import im.challenger.videostore.controller.fs.FileSystemStorageService;
import im.challenger.videostore.controller.fs.IStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.unit.DataSize;

import javax.servlet.MultipartConfigElement;

@Configuration
public class BeanConfig {

    @Autowired
    private Environment env;

    @Bean
    public AmazonS3 amazonS3Client() {
        AWSCredentials credentials = new BasicAWSCredentials(env.getProperty("aws.access.key"), env.getProperty("aws.secret.key"));
        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.EU_CENTRAL_1)
                .build();
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.ofMegabytes(200));
        factory.setMaxRequestSize(DataSize.ofMegabytes(200));
        factory.setLocation("/opt/challenger/tmp");
        return factory.createMultipartConfig();
    }

    @Bean
    public IStorageService storageService() {
        return new AwsS3StorageService();
        // return new FileSystemStorageService();
    }

}
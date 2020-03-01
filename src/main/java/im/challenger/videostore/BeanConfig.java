package im.challenger.videostore;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.unit.DataSize;

import javax.servlet.MultipartConfigElement;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

@Configuration
public class BeanConfig {

    @Autowired
    private Environment environment;

    @Bean
    public CloudBlobClient cloudBlobClient() throws URISyntaxException, InvalidKeyException {
        CloudStorageAccount storageAccount = CloudStorageAccount.parse(environment.getProperty("azure.storage.ConnectionString"));
        return storageAccount.createCloudBlobClient();
    }

    @Bean
    public CloudBlobContainer videosBlobContainer() throws URISyntaxException, InvalidKeyException, StorageException {
        return cloudBlobClient().getContainerReference(environment.getProperty("azure.storage.container.name"));
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.ofMegabytes(100));
        factory.setMaxRequestSize(DataSize.ofMegabytes(100));
        return factory.createMultipartConfig();
    }

}
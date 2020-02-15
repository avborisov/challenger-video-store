package im.challenger.videostore.controller.azure;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Service
public class BlobStorageService {

    @Autowired
    private CloudBlobContainer blobContainer;

    public URI upload(MultipartFile multipartFile) throws URISyntaxException, StorageException, IOException {
        CloudBlockBlob blob = blobContainer.getBlockBlobReference(multipartFile.getOriginalFilename());
        blob.upload(multipartFile.getInputStream(), -1);
        return blob.getUri();
    }

    public URI getBlobUri(String filename) throws URISyntaxException, StorageException {
        CloudBlockBlob blob = blobContainer.getBlockBlobReference(filename);
        return blob.getUri();
    }

}

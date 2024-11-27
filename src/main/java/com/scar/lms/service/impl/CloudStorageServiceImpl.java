package com.scar.lms.service.impl;

import com.google.auth.Credentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.scar.lms.service.CloudStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class CloudStorageServiceImpl implements CloudStorageService {

    @Value("${google.cloud.storage.bucket-name}")
    private String bucketName;

    private final Storage storage;

    public CloudStorageServiceImpl() throws IOException {

        InputStream credentialsStream = getClass().getClassLoader().getResourceAsStream("scar.json");
        if (credentialsStream == null) {
            throw new IOException("Credentials file not found in classpath.");
        }

        Credentials credentials = ServiceAccountCredentials.fromStream(credentialsStream);
        this.storage = StorageOptions.newBuilder()
                .setCredentials(credentials)
                .build()
                .getService();
    }

    public String uploadImage(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        Path tempFilePath = Files.createTempFile(fileName, null);
        Files.write(tempFilePath, file.getBytes());

        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo, Files.readAllBytes(tempFilePath));
        return "https://storage.cloud.google.com/" + bucketName + "/" + fileName;
    }

}

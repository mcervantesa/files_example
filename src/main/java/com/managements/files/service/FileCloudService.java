package com.managements.files.service;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.managements.files.exception.MyFileNotFoundException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.UUID;

@Service
public class FileCloudService {
    private String uploadFile(File file, String fileName) {

        try {
            ClassPathResource resource = new ClassPathResource("filesexample-1b869-firebase-adminsdk-4mcfh-86e3c038e4.json");

            // Obtener el archivo del recurso
            File file2 = resource.getFile();

            String DOWNLOAD_URL = "https://firebasestorage.googleapis.com/v0/b/filesexample-1b869.appspot.com/o/%s?alt=media";
            BlobId blobId = BlobId.of("filesexample-1b869.appspot.com", fileName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("media").build();
            Credentials credentials = GoogleCredentials.fromStream(new FileInputStream(file2));
            Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
            storage.create(blobInfo, Files.readAllBytes(file.toPath()));
            return String.format(DOWNLOAD_URL, URLEncoder.encode(fileName, StandardCharsets.UTF_8));

        } catch (Exception e) {
            throw new MyFileNotFoundException(e.getMessage());
        }

    }

    private File convertToFile(MultipartFile multipartFile, String fileName) {
        try {
            File tempFile = new File(fileName);
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(multipartFile.getBytes());
                fos.close();
            }
            return tempFile;

        } catch (Exception e) {
            throw new MyFileNotFoundException(e.getMessage());
        }
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }


    public String upload(MultipartFile multipartFile) {
        try {
            String fileName = multipartFile.getOriginalFilename();                        // to get original file name
            fileName = UUID.randomUUID().toString().concat(this.getExtension(fileName));  // to generated random string values for file name.

            File file = this.convertToFile(multipartFile, fileName);                      // to convert multipartFile to File
            String URL = this.uploadFile(file, fileName);                                   // to get uploaded file link
            file.delete();
            return URL;
        } catch (Exception e) {

            throw new MyFileNotFoundException("Image couldn't upload, Something went wrong", e);
        }
    }
}

package com.managements.files.service;

import com.managements.files.dto.FileUploadResponseDTO;
import com.managements.files.dto.LoadFile;
import com.managements.files.exception.MyFileNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
public class FilesImplService implements IFilesService{

    @Autowired
    private FilesDBService filesDBService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private FileCloudService cloudService;

    @Override
    public FileUploadResponseDTO uploadFile(MultipartFile multipartFile, String storage)  {

        switch (storage){

            case "db":
                return database(multipartFile);
            case "local":
                return local(multipartFile);

            case "cloud":
                return cloud(multipartFile);
            default:

                return null;
        }

    }

    private FileUploadResponseDTO database(MultipartFile multipartFile)  {

        try {
            String response = filesDBService.addFile(multipartFile);
            String url = "http://localhost:8080/file/download/"+response;
           return new FileUploadResponseDTO(multipartFile.getOriginalFilename(), url , multipartFile.getSize());

        }catch(Exception e){
            throw new MyFileNotFoundException(e.getMessage());
        }

    }
    private FileUploadResponseDTO local(MultipartFile multipartFile){

        String fileName = fileStorageService.storeFile(multipartFile);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(fileName)
                .toUriString();

        return new FileUploadResponseDTO(fileName,fileDownloadUri, multipartFile.getSize());
    }
    private FileUploadResponseDTO cloud(MultipartFile multipartFile){
        String url = cloudService.upload(multipartFile);
        return new FileUploadResponseDTO(multipartFile.getName(),url,multipartFile.getSize());
    }

}

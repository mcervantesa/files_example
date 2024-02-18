package com.managements.files.dto;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FileUploadResponseDTO {

    private String fileName;
    private String downloadUri;
    private long size;

    public FileUploadResponseDTO(String fileName, String downloadUri, long size) {
        this.fileName = fileName;
        this.downloadUri = downloadUri;
        this.size = size;
    }
}

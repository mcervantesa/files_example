package com.managements.files.service;

import com.managements.files.dto.FileUploadResponseDTO;
import org.springframework.web.multipart.MultipartFile;

public interface IFilesService {
    FileUploadResponseDTO uploadFile(MultipartFile multipartFile, String storage);
}

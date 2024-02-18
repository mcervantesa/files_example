package com.managements.files.controller;

import com.managements.files.dto.FileUploadResponseDTO;
import com.managements.files.dto.LoadFile;
import com.managements.files.service.FilesDBService;
import com.managements.files.service.IFilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/files")
public class FilesController {


    @Autowired
    private IFilesService service;

    @Autowired
    private FilesDBService fileService;

    @PostMapping("/upload/{storage}")
    public ResponseEntity<FileUploadResponseDTO> uploadFile(
            @RequestParam("file") MultipartFile multipartFile,
            @PathVariable(required = true) String storage ) {
                return new ResponseEntity<>(service.uploadFile(multipartFile,storage), HttpStatus.OK);
    }
    @GetMapping("/download/{id}")
    public ResponseEntity<ByteArrayResource> download(@PathVariable String id) throws IOException {
        LoadFile loadFile = fileService.downloadFile(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(loadFile.getFileType() ))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + loadFile.getFilename() + "\"")
                .body(new ByteArrayResource(loadFile.getFile()));
    }
}

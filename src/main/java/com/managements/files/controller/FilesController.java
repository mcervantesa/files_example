package com.managements.files.controller;

import com.managements.files.dto.FileUploadResponseDTO;
import com.managements.files.dto.LoadFile;
import com.managements.files.service.FilesDBService;
import com.managements.files.service.IFilesService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(
            summary = "File upload endpoint",
            description = "File upload endpoint",
            tags = { "Upload files" })
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "403", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "404", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }) })
    @PostMapping(value ="/upload/{storage}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @CircuitBreaker(name = "randomActivity", fallbackMethod = "fallbackRandomActivity")
    public ResponseEntity<FileUploadResponseDTO> uploadFile(
            @Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            @RequestPart(value = "file") MultipartFile file,
            @RequestPart(value = "storage" ) String storage
    ) {
                return new ResponseEntity<>(service.uploadFile(file,storage), HttpStatus.OK);
    }
    @GetMapping("/download/{id}")
    public ResponseEntity<ByteArrayResource> download(@PathVariable String id) throws IOException {
        LoadFile loadFile = fileService.downloadFile(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(loadFile.getFileType() ))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + loadFile.getFilename() + "\"")
                .body(new ByteArrayResource(loadFile.getFile()));
    }
    public String fallbackRandomActivity(Throwable throwable) {
        return "File not updload";
    }
}

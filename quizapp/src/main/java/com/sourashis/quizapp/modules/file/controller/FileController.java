package com.sourashis.quizapp.modules.file.controller;

import com.sourashis.quizapp.core.audit.Auditable;
import com.sourashis.quizapp.core.response.ApiResponseWrapper;
import com.sourashis.quizapp.modules.file.dto.FileResponse;
import com.sourashis.quizapp.modules.file.entity.File;
import com.sourashis.quizapp.modules.file.repository.FileRepository;
import com.sourashis.quizapp.modules.file.service.FileService;
import com.sourashis.quizapp.infrastructure.storage.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Tag(name = "Files", description = "File upload, retrieval, and deletion endpoints")
@RestController
@RequestMapping("/api/v1/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Operation(summary = "Upload a file", description = "Uploads a file and optionally associates a file type")
    @ApiResponse(responseCode = "201", description = "File uploaded successfully")
    @Auditable(action = "UPLOAD", resourceType = "FILE")
    @PostMapping("/upload")
    @PreAuthorize("hasAuthority('file:upload')")
    public ResponseEntity<ApiResponseWrapper<FileResponse>> uploadFile(
            @RequestParam("file") @Parameter(description = "The file to upload") MultipartFile file,
            @RequestParam(required = false) @Parameter(description = "Optional file type identifier") String fileType) {
        FileResponse response = fileService.uploadFile(file, fileType);
        return ApiResponseWrapper.created(response, "File uploaded successfully");
    }

    @Operation(summary = "Get user files", description = "Retrieves all files belonging to a specific user")
    @ApiResponse(responseCode = "200", description = "Files retrieved successfully")
    @Auditable(action = "READ", resourceType = "FILE")
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponseWrapper<List<FileResponse>>> getUserFiles(
            @PathVariable @Parameter(description = "ID of the user") Long userId) {
        List<FileResponse> files = fileService.getUserFiles(userId);
        return ApiResponseWrapper.success(files, "Files retrieved successfully");
    }

    @Operation(summary = "Get file by UUID", description = "Retrieves file metadata by its UUID")
    @ApiResponse(responseCode = "200", description = "File retrieved successfully")
    @Auditable(action = "READ", resourceType = "FILE")
    @GetMapping("/{uuid}")
    public ResponseEntity<ApiResponseWrapper<FileResponse>> getFile(
            @PathVariable @Parameter(description = "UUID of the file") String uuid) {
        FileResponse file = fileService.getFileByUuid(uuid);
        return ApiResponseWrapper.success(file, "File retrieved successfully");
    }

    @Operation(summary = "Download file by UUID", description = "Downloads the file as a stream")
    @ApiResponse(responseCode = "200", description = "File downloaded successfully")
    @Auditable(action = "READ", resourceType = "FILE")
    @GetMapping("/{uuid}/download")
    public ResponseEntity<InputStreamResource> downloadFile(
            @PathVariable @Parameter(description = "UUID of the file to download") String uuid) throws IOException {
        File file = fileRepository.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("File not found with uuid: " + uuid));

        Path filePath = fileStorageService.load(file.getStoredName());
        java.io.File diskFile = filePath.toFile();
        if (!diskFile.exists()) {
            throw new RuntimeException("File not found on disk: " + file.getStoredName());
        }

        InputStreamResource resource = new InputStreamResource(new FileInputStream(diskFile));

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getMimeType()))
                .contentLength(file.getFileSize())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getOriginalName() + "\"")
                .body(resource);
    }

    @Operation(summary = "Delete a file", description = "Deletes a file by its UUID")
    @ApiResponse(responseCode = "200", description = "File deleted successfully")
    @Auditable(action = "DELETE", resourceType = "FILE")
    @DeleteMapping("/{uuid}")
    @PreAuthorize("hasAuthority('file:delete')")
    public ResponseEntity<ApiResponseWrapper<Void>> deleteFile(
            @PathVariable @Parameter(description = "UUID of the file to delete") String uuid) {
        fileService.deleteFile(uuid);
        return ApiResponseWrapper.success(null, "File deleted successfully");
    }
}

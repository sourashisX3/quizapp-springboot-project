package com.sourashis.quizapp.modules.file.controller;

import com.sourashis.quizapp.core.response.ApiResponseWrapper;
import com.sourashis.quizapp.modules.file.dto.FileResponse;
import com.sourashis.quizapp.modules.file.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    @PreAuthorize("hasAuthority('file:upload')")
    public ResponseEntity<ApiResponseWrapper<FileResponse>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String fileType) {
        FileResponse response = fileService.uploadFile(file, fileType);
        return ApiResponseWrapper.created(response, "File uploaded successfully");
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponseWrapper<List<FileResponse>>> getUserFiles(@PathVariable Long userId) {
        List<FileResponse> files = fileService.getUserFiles(userId);
        return ApiResponseWrapper.success(files, "Files retrieved successfully");
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ApiResponseWrapper<FileResponse>> getFile(@PathVariable String uuid) {
        FileResponse file = fileService.getFileByUuid(uuid);
        return ApiResponseWrapper.success(file, "File retrieved successfully");
    }

    @DeleteMapping("/{uuid}")
    @PreAuthorize("hasAuthority('file:delete')")
    public ResponseEntity<ApiResponseWrapper<Void>> deleteFile(@PathVariable String uuid) {
        fileService.deleteFile(uuid);
        return ApiResponseWrapper.success(null, "File deleted successfully");
    }
}

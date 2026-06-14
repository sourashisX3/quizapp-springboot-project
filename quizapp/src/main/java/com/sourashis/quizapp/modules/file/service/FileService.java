package com.sourashis.quizapp.modules.file.service;

import com.sourashis.quizapp.infrastructure.storage.FileStorageService;
import com.sourashis.quizapp.modules.auth.entity.User;
import com.sourashis.quizapp.modules.file.dto.FileResponse;
import com.sourashis.quizapp.modules.file.entity.File;
import com.sourashis.quizapp.modules.file.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class FileService {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FileStorageService fileStorageService;

    public FileResponse uploadFile(MultipartFile multipartFile, String fileType) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String storedName = fileStorageService.store(multipartFile);
        String originalName = multipartFile.getOriginalFilename() != null ? multipartFile.getOriginalFilename() : "unknown";
        String mimeType = multipartFile.getContentType() != null ? multipartFile.getContentType() : "application/octet-stream";

        String checksum = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(multipartFile.getBytes());
            checksum = HexFormat.of().formatHex(digest);
        } catch (Exception e) {
            checksum = null;
        }

        File file = File.builder()
                .uuid(UUID.randomUUID().toString())
                .userId(currentUser.getId())
                .originalName(originalName)
                .storedName(storedName)
                .mimeType(mimeType)
                .fileSize(multipartFile.getSize())
                .storageProvider("LOCAL")
                .storagePath(storedName)
                .fileType(fileType != null ? fileType : "GENERAL")
                .checksumMd5(checksum)
                .isPublic(false)
                .build();

        file = fileRepository.save(file);
        return toResponse(file);
    }

    @Transactional(readOnly = true)
    public List<FileResponse> getUserFiles(Long userId) {
        return fileRepository.findByUserId(userId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public FileResponse getFileByUuid(String uuid) {
        File file = fileRepository.findByUuid(uuid).orElseThrow();
        return toResponse(file);
    }

    public void deleteFile(String uuid) {
        File file = fileRepository.findByUuid(uuid).orElseThrow();
        fileStorageService.delete(file.getStoredName());
        fileRepository.delete(file);
    }

    private FileResponse toResponse(File file) {
        return FileResponse.builder()
                .id(file.getId())
                .uuid(file.getUuid())
                .originalName(file.getOriginalName())
                .storedName(file.getStoredName())
                .mimeType(file.getMimeType())
                .fileSize(file.getFileSize())
                .fileType(file.getFileType())
                .isPublic(file.getIsPublic())
                .createdAt(file.getCreatedAt())
                .build();
    }
}

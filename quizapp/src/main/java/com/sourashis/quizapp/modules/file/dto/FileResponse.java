package com.sourashis.quizapp.modules.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileResponse {
    @Schema(description = "File ID", example = "1")
    private Long id;
    @Schema(description = "File UUID for public reference", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String uuid;
    @Schema(description = "Original file name as uploaded", example = "profile-pic.jpg")
    private String originalName;
    @Schema(description = "Unique storage name on disk", example = "uuid-profile-pic.jpg")
    private String storedName;
    @Schema(description = "MIME type of the file", example = "image/jpeg")
    private String mimeType;
    @Schema(description = "File size in bytes", example = "204800")
    private Long fileSize;
    @Schema(description = "File type classification", example = "AVATAR")
    private String fileType;
    @Schema(description = "Whether the file is publicly accessible", example = "true")
    private Boolean isPublic;
    @Schema(description = "Upload timestamp", example = "2026-06-15T10:30:00Z")
    private Instant createdAt;
}

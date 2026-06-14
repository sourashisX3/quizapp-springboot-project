package com.sourashis.quizapp.modules.file.dto;

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
    private Long id;
    private String uuid;
    private String originalName;
    private String storedName;
    private String mimeType;
    private Long fileSize;
    private String fileType;
    private Boolean isPublic;
    private Instant createdAt;
}

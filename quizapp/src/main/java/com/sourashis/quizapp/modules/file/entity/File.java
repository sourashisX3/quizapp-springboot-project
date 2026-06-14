package com.sourashis.quizapp.modules.file.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "files")
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String uuid;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 500)
    private String originalName;

    @Column(nullable = false, length = 500)
    private String storedName;

    @Column(nullable = false, length = 100)
    private String mimeType;

    @Column(nullable = false)
    private Long fileSize;

    @Builder.Default
    private String storageProvider = "LOCAL";

    @Column(nullable = false, length = 1000)
    private String storagePath;

    @Column(nullable = false)
    private String fileType;

    @Column(length = 32)
    private String checksumMd5;

    @Builder.Default
    private Boolean isPublic = false;

    @Column(updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}

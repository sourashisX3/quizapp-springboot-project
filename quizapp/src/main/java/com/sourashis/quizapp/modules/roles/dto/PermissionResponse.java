package com.sourashis.quizapp.modules.roles.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionResponse {

    @Schema(description = "Permission ID", example = "1")
    private Long id;

    @Schema(description = "Permission name (e.g., quiz:create)", example = "quiz:create")
    private String name;

    @Schema(description = "Resource this permission applies to", example = "quiz")
    private String resource;

    @Schema(description = "Action allowed on the resource", example = "create")
    private String action;

    @Schema(description = "Human-readable description of the permission", example = "Allows creating new quizzes")
    private String description;
}

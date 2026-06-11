package com.sourashis.quizapp.modules.roles.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PermissionRequest {
    @NotBlank(message = "Permission name is required")
    private String name;
    private String description;
}

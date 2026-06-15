package com.sourashis.quizapp.modules.roles.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.Set;

@Data
public class RoleRequest {

    @NotBlank
    @Schema(description = "Unique role name (e.g., ROLE_USER, ROLE_ADMIN)", example = "ROLE_MODERATOR")
    private String name;

    @Schema(description = "Description of the role's purpose and permissions", example = "Content moderator with quiz management permissions")
    private String description;

    @Schema(description = "Set of permission names to assign to this role", example = "[quiz:create, quiz:read, quiz:update, question:read]")
    private Set<String> permissionNames;
}

package com.sourashis.quizapp.modules.roles.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolesResponse {

    @Schema(description = "Role ID", example = "1")
    private Long id;

    @Schema(description = "Role name", example = "ROLE_USER")
    private String name;

    @Schema(description = "Role description", example = "Default user role with basic permissions")
    private String description;

    @Schema(description = "Set of permission names assigned to this role", example = "[quiz:read, quiz:attempt, category:read]")
    private Set<String> permissions;
}

package com.sourashis.quizapp.modules.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for admin user update")
public class AdminUserUpdateRequest {

    @Schema(description = "Display name", example = "John Doe")
    private String displayName;

    @Schema(description = "Email address", example = "john@example.com")
    private String email;

    @Schema(description = "Account status", example = "ACTIVE")
    private String accountStatus;

    @Schema(description = "Role ID to assign", example = "2")
    private Long roleId;
}

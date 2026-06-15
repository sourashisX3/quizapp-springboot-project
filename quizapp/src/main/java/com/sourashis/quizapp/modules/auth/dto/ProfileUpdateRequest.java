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
@Schema(description = "Request body for updating user profile")
public class ProfileUpdateRequest {

    @Schema(description = "Display name shown on the profile", example = "John Doe")
    private String displayName;

    @Schema(description = "Short biography", example = "Quiz enthusiast and lifelong learner")
    private String bio;

    @Schema(description = "Phone number", example = "+1234567890")
    private String phoneNumber;

    @Schema(description = "Physical address", example = "123 Main St, City")
    private String address;

    @Schema(description = "JSON string containing social media links", example = "{\"github\":\"https://github.com/johndoe\"}")
    private String socialLinksJson;
}

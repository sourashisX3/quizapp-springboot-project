package com.sourashis.quizapp.modules.activity.dto;

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
@Schema(description = "Response containing an activity log entry")
public class ActivityLogResponse {

    @Schema(description = "Activity log ID", example = "1")
    private Long id;

    @Schema(description = "Type of activity", example = "QUIZ_COMPLETED")
    private String activityType;

    @Schema(description = "Description of the activity", example = "Completed quiz 'Java Basics'")
    private String description;

    @Schema(description = "Reference entity ID", example = "42")
    private Long referenceId;

    @Schema(description = "Reference entity type", example = "QUIZ")
    private String referenceType;

    @Schema(description = "Additional metadata JSON", example = "{\"score\": 85}")
    private String metadataJson;

    @Schema(description = "When the activity occurred", example = "2025-06-15T10:30:00Z")
    private Instant createdAt;
}

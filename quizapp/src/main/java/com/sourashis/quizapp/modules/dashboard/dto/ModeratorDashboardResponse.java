package com.sourashis.quizapp.modules.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Moderator dashboard response with content management statistics")
public class ModeratorDashboardResponse {
    @Schema(description = "Total number of questions managed", example = "350")
    private long totalQuestions;
    @Schema(description = "Total number of quizzes managed", example = "45")
    private long totalQuizzes;
    @Schema(description = "Number of currently active contests", example = "3")
    private long totalActiveContests;
}

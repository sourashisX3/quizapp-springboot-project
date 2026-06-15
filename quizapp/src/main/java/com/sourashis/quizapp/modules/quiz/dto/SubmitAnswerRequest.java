package com.sourashis.quizapp.modules.quiz.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmitAnswerRequest {

    @NotNull
    @Schema(description = "ID of the question being answered", example = "1")
    private Long questionId;

    @Schema(description = "ID of the selected option (for MCQ)", example = "2")
    private Long selectedOptionId;

    @Schema(description = "Text answer (for subjective questions)", example = "Spring Boot simplifies configuration")
    private String answerText;
}

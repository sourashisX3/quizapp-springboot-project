package com.sourashis.quizapp.modules.question.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponse {

    private Long id;
    private String uuid;
    private String title;
    private Long categoryId;
    private String categoryName;
    private String difficulty;
    private String questionType;
    private Integer timeLimitSeconds;
    private Integer points;
    private String tags;
    private String explanation;
    private boolean isActive;
    private List<OptionResponse> options;
    private Instant createdAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OptionResponse {

        private Long id;
        private String optionText;
        private Integer sortOrder;
        private String explanation;
    }
}

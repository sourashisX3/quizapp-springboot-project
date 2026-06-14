package com.sourashis.quizapp.modules.question.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionListResponse {

    private Long id;
    private String uuid;
    private String title;
    private Long categoryId;
    private String categoryName;
    private String difficulty;
    private String questionType;
    private String tags;
    private Instant createdAt;
}

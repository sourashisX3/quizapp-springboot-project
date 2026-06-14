package com.sourashis.quizapp.modules.quiz.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryRequest {

    @NotBlank
    private String categoryName;

    private String description;

    private String iconUrl;

    private Long parentId;
}

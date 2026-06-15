package com.sourashis.quizapp.modules.quiz.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {

    @Schema(description = "Category ID", example = "1")
    private Long id;
    @Schema(description = "Category name", example = "Java")
    private String categoryName;
    @Schema(description = "Category description", example = "Java programming language and ecosystem")
    private String description;
    @Schema(description = "Icon URL", example = "https://example.com/icons/java.png")
    private String iconUrl;
    @Schema(description = "Parent category ID", example = "null")
    private Long parentId;
}

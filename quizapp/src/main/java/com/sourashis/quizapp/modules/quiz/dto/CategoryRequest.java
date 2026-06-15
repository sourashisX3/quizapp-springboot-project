package com.sourashis.quizapp.modules.quiz.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryRequest {

    @NotBlank
    @Schema(description = "Name of the category", example = "Java")
    private String categoryName;

    @Schema(description = "Description of the category", example = "Java programming language and ecosystem")
    private String description;

    @Schema(description = "URL to an icon image for the category", example = "https://example.com/icons/java.png")
    private String iconUrl;

    @Schema(description = "ID of the parent category (for subcategories)", example = "null")
    private Long parentId;
}

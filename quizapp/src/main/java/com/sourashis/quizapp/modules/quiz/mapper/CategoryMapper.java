package com.sourashis.quizapp.modules.quiz.mapper;

import com.sourashis.quizapp.modules.quiz.dto.CategoryResponse;
import com.sourashis.quizapp.modules.quiz.entity.Category;

public final class CategoryMapper {

    private CategoryMapper() {}

    public static CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .categoryName(category.getName())
                .description(category.getDescription())
                .iconUrl(category.getIconUrl())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .build();
    }
}

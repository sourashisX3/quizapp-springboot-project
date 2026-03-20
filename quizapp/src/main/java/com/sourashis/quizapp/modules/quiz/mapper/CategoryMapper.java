package com.sourashis.quizapp.modules.quiz.mapper;

import com.sourashis.quizapp.modules.quiz.dto.CategoryResponse;
import com.sourashis.quizapp.modules.quiz.entity.Category;

public class CategoryMapper {

    private CategoryMapper() {
    }

    public static CategoryResponse toCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .categoryName(category.getCategoryName())
                .build();
    }
}

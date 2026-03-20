package com.sourashis.quizapp.modules.question.mapper;

import com.sourashis.quizapp.modules.question.dto.QuestionRequest;
import com.sourashis.quizapp.modules.question.dto.QuestionResponse;
import com.sourashis.quizapp.modules.question.entity.DifficultyLevel;
import com.sourashis.quizapp.modules.question.entity.Question;
import com.sourashis.quizapp.modules.question.exception.CategoryNotFoundException;
import com.sourashis.quizapp.modules.quiz.entity.Category;
import com.sourashis.quizapp.modules.quiz.repository.CategoryRepository;

/**
 * Mapper for converting Question entity to/from DTOs.
 * Handles all conversions between different representations of Question data.
 * Follows architectural pattern of separating concerns between layers.
 */
public class QuestionMapper {

    private QuestionMapper() {}

    /**
     * Convert QuestionRequest to Question entity.
     * Validates that the category exists before creating the entity.
     *
     * @param request the QuestionRequest DTO
     * @param categoryRepository the repository to fetch category
     * @return the Question entity
     * @throws CategoryNotFoundException if category with given id doesn't exist
     * @throws IllegalArgumentException if difficulty level is invalid
     */
    public static Question toEntity(QuestionRequest request, CategoryRepository categoryRepository) {
        // Validate and fetch category from database
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(request.getCategoryId()));

        // Validate and convert difficulty level string to enum
        DifficultyLevel difficultyLevel = DifficultyLevel.fromString(request.getDifficultyLevel());

        // Create and populate question entity
        Question question = new Question();
        question.setQuestionTitle(request.getQuestionTitle());
        question.setOption1(request.getOption1());
        question.setOption2(request.getOption2());
        question.setOption3(request.getOption3());
        question.setOption4(request.getOption4());
        question.setRightAnswer(request.getRightAnswer());
        question.setDifficultyLevel(difficultyLevel);
        question.setCategory(category);

        return question;
    }

    /**
     * Convert Question entity to QuestionResponse DTO.
     * Safely extracts category information and handles null values.
     *
     * @param question the Question entity
     * @return the QuestionResponse DTO
     */
    public static QuestionResponse toResponse(Question question) {
        return QuestionResponse.builder()
                .id(question.getId())
                .questionTitle(question.getQuestionTitle())
                .option1(question.getOption1())
                .option2(question.getOption2())
                .option3(question.getOption3())
                .option4(question.getOption4())
                .difficultyLevel(question.getDifficultyLevel() != null 
                        ? question.getDifficultyLevel().toString() 
                        : null)
                .categoryName(question.getCategory() != null 
                        ? question.getCategory().getCategoryName() 
                        : null)
                .categoryId(question.getCategory() != null 
                        ? question.getCategory().getId() 
                        : null)
                .build();
    }
}


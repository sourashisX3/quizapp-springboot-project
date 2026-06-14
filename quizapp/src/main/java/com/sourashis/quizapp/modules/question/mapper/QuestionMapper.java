package com.sourashis.quizapp.modules.question.mapper;

import com.sourashis.quizapp.modules.question.dto.QuestionListResponse;
import com.sourashis.quizapp.modules.question.dto.QuestionRequest;
import com.sourashis.quizapp.modules.question.dto.QuestionResponse;
import com.sourashis.quizapp.modules.question.entity.DifficultyLevel;
import com.sourashis.quizapp.modules.question.entity.Question;
import com.sourashis.quizapp.modules.quiz.entity.Category;

import java.util.List;
import java.util.stream.Collectors;

public final class QuestionMapper {

    private QuestionMapper() {}

    public static Question toEntity(QuestionRequest req, Category category) {
        return Question.builder()
                .title(req.getTitle())
                .category(category)
                .difficulty(DifficultyLevel.fromString(req.getDifficulty()))
                .questionType(req.getQuestionType())
                .timeLimitSeconds(req.getTimeLimitSeconds())
                .points(req.getPoints())
                .tags(req.getTags())
                .explanation(req.getExplanation())
                .build();
    }

    public static QuestionResponse toResponse(Question q) {
        List<QuestionResponse.OptionResponse> optionResponses = q.getOptions().stream()
                .map(opt -> QuestionResponse.OptionResponse.builder()
                        .id(opt.getId())
                        .optionText(opt.getOptionText())
                        .sortOrder(opt.getSortOrder())
                        .explanation(opt.getExplanation())
                        .build())
                .collect(Collectors.toList());

        return QuestionResponse.builder()
                .id(q.getId())
                .uuid(q.getUuid())
                .title(q.getTitle())
                .categoryId(q.getCategory().getId())
                .categoryName(q.getCategory().getName())
                .difficulty(q.getDifficulty().name())
                .questionType(q.getQuestionType())
                .timeLimitSeconds(q.getTimeLimitSeconds())
                .points(q.getPoints())
                .tags(q.getTags())
                .explanation(q.getExplanation())
                .isActive(q.isActive())
                .options(optionResponses)
                .createdAt(q.getCreatedAt())
                .build();
    }

    public static QuestionListResponse toListResponse(Question q) {
        return QuestionListResponse.builder()
                .id(q.getId())
                .uuid(q.getUuid())
                .title(q.getTitle())
                .categoryId(q.getCategory().getId())
                .categoryName(q.getCategory().getName())
                .difficulty(q.getDifficulty().name())
                .questionType(q.getQuestionType())
                .tags(q.getTags())
                .createdAt(q.getCreatedAt())
                .build();
    }
}

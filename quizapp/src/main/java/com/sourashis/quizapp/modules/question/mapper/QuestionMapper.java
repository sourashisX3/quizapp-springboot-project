package com.sourashis.quizapp.modules.question.mapper;

import com.sourashis.quizapp.modules.question.dto.QuestionRequest;
import com.sourashis.quizapp.modules.question.dto.QuestionResponse;
import com.sourashis.quizapp.modules.question.entity.Question;

public class QuestionMapper {

    private QuestionMapper() {}

    public static Question toEntity(QuestionRequest request) {
        Question question = new Question();
        question.setQuestionTitle(request.getQuestionTitle());
        question.setOption1(request.getOption1());
        question.setOption2(request.getOption2());
        question.setOption3(request.getOption3());
        question.setOption4(request.getOption4());
        question.setRightAnswer(request.getRightAnswer());
        question.setDifficultyLevel(request.getDifficultyLevel());
        question.setCategory(request.getCategory());
        return question;
    }

    public static QuestionResponse toResponse(Question question) {
        return QuestionResponse.builder()
                .id(question.getId())
                .questionTitle(question.getQuestionTitle())
                .option1(question.getOption1())
                .option2(question.getOption2())
                .option3(question.getOption3())
                .option4(question.getOption4())
                .difficultyLevel(question.getDifficultyLevel())
                .category(question.getCategory())
                .build();
    }
}


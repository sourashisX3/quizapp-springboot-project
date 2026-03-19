package com.sourashis.quizapp.modules.quiz.mapper;

import com.sourashis.quizapp.modules.question.entity.Question;
import com.sourashis.quizapp.modules.quiz.dto.QuizQuestionResponse;
import com.sourashis.quizapp.modules.quiz.dto.QuizResponse;
import com.sourashis.quizapp.modules.quiz.entity.Quiz;

import java.util.List;

public class QuizMapper {

    private QuizMapper() {}

    public static QuizQuestionResponse toQuestionResponse(Question question) {
        return QuizQuestionResponse.builder()
                .id(question.getId())
                .questionTitle(question.getQuestionTitle())
                .option1(question.getOption1())
                .option2(question.getOption2())
                .option3(question.getOption3())
                .option4(question.getOption4())
                .build();
    }

    public static QuizResponse toResponse(Quiz quiz) {
        List<QuizQuestionResponse> questions = quiz.getQuestions()
                .stream()
                .map(QuizMapper::toQuestionResponse)
                .toList();

        return QuizResponse.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .questions(questions)
                .build();
    }
}


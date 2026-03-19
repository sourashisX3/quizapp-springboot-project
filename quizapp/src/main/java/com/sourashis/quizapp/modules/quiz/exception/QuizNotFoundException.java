package com.sourashis.quizapp.modules.quiz.exception;

import com.sourashis.quizapp.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class QuizNotFoundException extends BaseException {

    public QuizNotFoundException(Integer id) {
        super("Quiz not found with id: " + id, HttpStatus.NOT_FOUND);
    }

    public QuizNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}


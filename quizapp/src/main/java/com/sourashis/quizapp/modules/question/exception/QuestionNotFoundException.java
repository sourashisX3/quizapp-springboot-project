package com.sourashis.quizapp.modules.question.exception;

import com.sourashis.quizapp.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class QuestionNotFoundException extends BaseException {

    public QuestionNotFoundException(Integer id) {
        super("Question not found with id: " + id, HttpStatus.NOT_FOUND);
    }

    public QuestionNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}


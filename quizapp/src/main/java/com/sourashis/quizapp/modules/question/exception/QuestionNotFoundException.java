package com.sourashis.quizapp.modules.question.exception;

import com.sourashis.quizapp.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class QuestionNotFoundException extends BaseException {

    public QuestionNotFoundException(Long id) {
        super("Question not found with id: " + id, HttpStatus.NOT_FOUND);
    }
}

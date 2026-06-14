package com.sourashis.quizapp.modules.quiz.exception;

import com.sourashis.quizapp.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class QuizNotFoundException extends BaseException {

    public QuizNotFoundException(Long id) {
        super("Quiz with id '" + id + "' not found", HttpStatus.NOT_FOUND);
    }

    public QuizNotFoundException(String uuid) {
        super("Quiz with uuid '" + uuid + "' not found", HttpStatus.NOT_FOUND);
    }
}

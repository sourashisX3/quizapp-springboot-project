package com.sourashis.quizapp.modules.quiz.exception;

import com.sourashis.quizapp.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class MaxAttemptsExceededException extends BaseException {

    public MaxAttemptsExceededException(int maxAttempts) {
        super("Maximum attempts (" + maxAttempts + ") exceeded for this quiz", HttpStatus.BAD_REQUEST);
    }
}

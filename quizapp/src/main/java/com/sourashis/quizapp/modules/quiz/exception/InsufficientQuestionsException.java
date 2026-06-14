package com.sourashis.quizapp.modules.quiz.exception;

import com.sourashis.quizapp.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InsufficientQuestionsException extends BaseException {

    public InsufficientQuestionsException(int requested, int available) {
        super("Insufficient questions: requested " + requested + " but only " + available + " available", HttpStatus.BAD_REQUEST);
    }
}

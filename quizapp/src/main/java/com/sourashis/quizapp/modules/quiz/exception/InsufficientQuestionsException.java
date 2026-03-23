package com.sourashis.quizapp.modules.quiz.exception;

import com.sourashis.quizapp.core.exception.BaseException;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when there are insufficient questions available in a category for quiz creation.
 */
public class InsufficientQuestionsException extends BaseException {

    public InsufficientQuestionsException(Integer categoryId, Integer requiredQuestions, Integer availableQuestions) {
        super(
                "Category ID: " + categoryId + " has only " + availableQuestions + 
                " questions available, but " + requiredQuestions + " are required to create a quiz",
                HttpStatus.BAD_REQUEST
        );
    }

    public InsufficientQuestionsException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}


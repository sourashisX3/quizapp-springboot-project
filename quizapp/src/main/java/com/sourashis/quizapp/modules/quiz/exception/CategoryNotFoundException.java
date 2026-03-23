package com.sourashis.quizapp.modules.quiz.exception;

import com.sourashis.quizapp.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CategoryNotFoundException extends BaseException {

    public CategoryNotFoundException(Integer id) {
        super("Category not found with id: " + id, HttpStatus.NOT_FOUND);
    }

    public CategoryNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}

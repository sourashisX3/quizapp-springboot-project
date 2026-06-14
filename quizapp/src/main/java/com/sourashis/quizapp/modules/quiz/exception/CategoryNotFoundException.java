package com.sourashis.quizapp.modules.quiz.exception;

import com.sourashis.quizapp.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CategoryNotFoundException extends BaseException {

    public CategoryNotFoundException(Long id) {
        super("Category with id '" + id + "' not found", HttpStatus.NOT_FOUND);
    }

    public CategoryNotFoundException(String name) {
        super("Category with name '" + name + "' not found", HttpStatus.NOT_FOUND);
    }
}

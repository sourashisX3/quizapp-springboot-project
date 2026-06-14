package com.sourashis.quizapp.modules.quiz.exception;

import com.sourashis.quizapp.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CategoryExistsException extends BaseException {

    public CategoryExistsException(String name) {
        super("Category with name '" + name + "' already exists", HttpStatus.CONFLICT);
    }
}

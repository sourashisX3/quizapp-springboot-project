package com.sourashis.quizapp.modules.question.exception;

import com.sourashis.quizapp.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CategoryNotFoundException extends BaseException {

    public CategoryNotFoundException(Long id) {
        super("Category not found with id: " + id, HttpStatus.NOT_FOUND);
    }

    public CategoryNotFoundException(String name) {
        super("Category not found: " + name, HttpStatus.NOT_FOUND);
    }
}

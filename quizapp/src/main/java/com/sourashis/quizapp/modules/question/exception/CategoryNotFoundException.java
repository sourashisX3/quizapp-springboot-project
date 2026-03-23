package com.sourashis.quizapp.modules.question.exception;

import com.sourashis.quizapp.core.exception.BaseException;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a referenced category is not found.
 */
public class CategoryNotFoundException extends BaseException {

    public CategoryNotFoundException(Integer categoryId) {
        super("Category not found with id: " + categoryId, HttpStatus.NOT_FOUND);
    }

    public CategoryNotFoundException(String categoryName) {
        super("Category not found with name: " + categoryName, HttpStatus.NOT_FOUND);
    }
}


package com.sourashis.quizapp.modules.quiz.exception;

import com.sourashis.quizapp.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CategoryNamesAreSameException extends BaseException {

    public CategoryNamesAreSameException(String name) {
        super("Category name '" + name + "' is the same as the current name", HttpStatus.BAD_REQUEST);
    }
}

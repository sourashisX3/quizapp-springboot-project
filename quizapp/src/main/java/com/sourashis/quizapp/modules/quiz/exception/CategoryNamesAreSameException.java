package com.sourashis.quizapp.modules.quiz.exception;

import com.sourashis.quizapp.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CategoryNamesAreSameException extends BaseException {
    public CategoryNamesAreSameException(String categoryName) {
        super("Category name is same as existing '"+ categoryName + "'" , HttpStatus.CONFLICT);
    }
}

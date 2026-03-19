package com.sourashis.quizapp.modules.auth.exception;

import com.sourashis.quizapp.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BaseException {

    public UserNotFoundException(Integer id) {
        super("User not found with id: " + id, HttpStatus.NOT_FOUND);
    }

    public UserNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}

package com.sourashis.quizapp.modules.auth.exception;

import com.sourashis.quizapp.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class UsernameAlreadyExistsException extends BaseException {
    public UsernameAlreadyExistsException(String message, HttpStatus status) {
        super(message, HttpStatus.CONFLICT);
    }
}

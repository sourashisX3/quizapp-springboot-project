package com.sourashis.quizapp.modules.auth.exception;

import com.sourashis.quizapp.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidRefreshTokenException extends BaseException {

    public InvalidRefreshTokenException(String msg) {
        super(msg, HttpStatus.UNAUTHORIZED);
    }
}

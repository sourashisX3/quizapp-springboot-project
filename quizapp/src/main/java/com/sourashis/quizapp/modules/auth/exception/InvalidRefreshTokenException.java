package com.sourashis.quizapp.modules.auth.exception;

public class InvalidRefreshTokenException extends RuntimeException {
    
    public InvalidRefreshTokenException(String message) {
        super(message);
    }

    public InvalidRefreshTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}


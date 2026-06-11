package com.sourashis.quizapp.modules.roles.exception;

import com.sourashis.quizapp.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class PermissionNotFoundException extends BaseException {

    public PermissionNotFoundException(String name) {
        super("Permission not found with name: " + name, HttpStatus.NOT_FOUND);
    }
}

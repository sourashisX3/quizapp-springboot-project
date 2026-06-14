package com.sourashis.quizapp.modules.roles.exception;

import com.sourashis.quizapp.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class RoleAlreadyExistsException extends BaseException {

    public RoleAlreadyExistsException(String name) {
        super("Role already exists with name: " + name, HttpStatus.CONFLICT);
    }
}

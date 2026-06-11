package com.sourashis.quizapp.modules.roles.exception;

import com.sourashis.quizapp.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class RoleAlreadyExistsException extends BaseException {

    public RoleAlreadyExistsException(String roleName) {
        super("Role '" + roleName + "' already exists", HttpStatus.CONFLICT);
    }
}

package com.sourashis.quizapp.modules.roles.exception;

import com.sourashis.quizapp.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class SystemRoleModificationException extends BaseException {

    public SystemRoleModificationException(String field, String name) {
        super("System role '" + name + "' cannot have its " + field + " changed", HttpStatus.BAD_REQUEST);
    }
}

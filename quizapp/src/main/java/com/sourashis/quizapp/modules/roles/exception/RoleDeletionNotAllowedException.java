package com.sourashis.quizapp.modules.roles.exception;

import com.sourashis.quizapp.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class RoleDeletionNotAllowedException extends BaseException {

    public RoleDeletionNotAllowedException(String name) {
        super("System role '" + name + "' cannot be deleted", HttpStatus.BAD_REQUEST);
    }
}

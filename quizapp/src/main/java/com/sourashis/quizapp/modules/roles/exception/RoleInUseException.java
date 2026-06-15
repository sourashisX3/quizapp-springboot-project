package com.sourashis.quizapp.modules.roles.exception;

import com.sourashis.quizapp.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class RoleInUseException extends BaseException {

    public RoleInUseException(String name, long userCount) {
        super("Role '" + name + "' is assigned to " + userCount + " user(s) and cannot be deleted", HttpStatus.CONFLICT);
    }
}

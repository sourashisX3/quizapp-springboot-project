package com.sourashis.quizapp.modules.roles.exception;

import com.sourashis.quizapp.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class RoleNotFoundException extends BaseException {

    public RoleNotFoundException(Integer id) {
        super("Role not found with id: " + id, HttpStatus.NOT_FOUND);
    }

    public RoleNotFoundException(String name) {
        super("Role not found with name: " + name, HttpStatus.NOT_FOUND);
    }
}

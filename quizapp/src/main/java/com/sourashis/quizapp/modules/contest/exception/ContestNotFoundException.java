package com.sourashis.quizapp.modules.contest.exception;

import com.sourashis.quizapp.core.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ContestNotFoundException extends BaseException {
    public ContestNotFoundException(Long id) {
        super("Contest not found with id: " + id, HttpStatus.NOT_FOUND);
    }
}

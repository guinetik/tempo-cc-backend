package com.guinetik.challenge.service.exeptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NO_CONTENT)
public class EmptyRoleException extends ServiceException {
    public EmptyRoleException(String message) {
        super(message);
    }
}

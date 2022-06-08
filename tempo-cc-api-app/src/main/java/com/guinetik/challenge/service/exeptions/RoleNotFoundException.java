package com.guinetik.challenge.service.exeptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class RoleNotFoundException extends ServiceException {
    public RoleNotFoundException(String message) {
        super(message);
    }
}

package com.guinetik.challenge.service.exeptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class MembershipNotFoundException extends ServiceException {
    public MembershipNotFoundException(String message) {
        super(message);
    }
}

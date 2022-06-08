package com.guinetik.challenge.service.exeptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ServiceRequestException extends ServiceException {
    public ServiceRequestException(String message) {
        super(message);
    }
}

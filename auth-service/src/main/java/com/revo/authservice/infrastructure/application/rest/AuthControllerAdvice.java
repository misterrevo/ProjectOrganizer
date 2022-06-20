package com.revo.authservice.infrastructure.application.rest;

import com.revo.authservice.domain.exception.BadLoginException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestControllerAdvice
class AuthControllerAdvice {

    @ExceptionHandler(BadLoginException.class)
    @ResponseStatus(UNAUTHORIZED)
    public String handleBadLoginException(BadLoginException exception){
        return exception.getMessage();
    }
}

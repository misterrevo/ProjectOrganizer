package com.revo.authservice.infrastructure.application.rest;

import com.revo.authservice.domain.exception.BadLoginException;
import com.revo.authservice.domain.exception.EmailInUseException;
import com.revo.authservice.domain.exception.UsernameInUseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestControllerAdvice
class AuthControllerAdvice {

    @ExceptionHandler(BadLoginException.class)
    @ResponseStatus(UNAUTHORIZED)
    public String handleBadLoginException(BadLoginException exception){
        return exception.getMessage();
    }

    @ExceptionHandler(EmailInUseException.class)
    @ResponseStatus(BAD_REQUEST)
    public String handleEmailInUseException(EmailInUseException exception){
        return exception.getMessage();
    }

    @ExceptionHandler(UsernameInUseException.class)
    @ResponseStatus(BAD_REQUEST)
    public String handleUsernameInUseException(UsernameInUseException exception){
        return exception.getMessage();
    }
}

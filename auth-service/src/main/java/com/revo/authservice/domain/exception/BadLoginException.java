package com.revo.authservice.domain.exception;

public class BadLoginException extends RuntimeException {
    private static final String MESSAGE = "Error while authenticating user!";

    public BadLoginException() {
        super(MESSAGE);
    }
}

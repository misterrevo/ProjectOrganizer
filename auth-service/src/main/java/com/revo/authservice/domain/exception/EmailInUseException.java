package com.revo.authservice.domain.exception;

public class EmailInUseException extends RuntimeException {
    private static final String MESSAGE = "Error while creating user, probably email: %s is in use!";

    public EmailInUseException(String email) {
        super(MESSAGE.formatted(email));
    }
}

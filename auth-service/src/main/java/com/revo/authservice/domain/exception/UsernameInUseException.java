package com.revo.authservice.domain.exception;

public class UsernameInUseException extends RuntimeException {
    private static final String MESSAGE = "Error while creating user, probably name: %s is in use!";

    public UsernameInUseException(String username) {
        super(MESSAGE.formatted(username));
    }
}

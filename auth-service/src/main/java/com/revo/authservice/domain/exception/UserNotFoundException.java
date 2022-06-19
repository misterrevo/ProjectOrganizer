package com.revo.authservice.domain.exception;

public class UserNotFoundException extends RuntimeException{
    private static final String MESSAGE = "Error while getting user, probably user: %s does not exists in base!";

    public UserNotFoundException(String subject) {
        super(MESSAGE.formatted(subject));
    }
}

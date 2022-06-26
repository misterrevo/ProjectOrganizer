package com.revo.projectservice.domain.exception;

public class NoPermissionException extends RuntimeException{
    private static final String MESSAGE = "You don't have permission to do that!";

    public NoPermissionException() {
        super(MESSAGE);
    }
}

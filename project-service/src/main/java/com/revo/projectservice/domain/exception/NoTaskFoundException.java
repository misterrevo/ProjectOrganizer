package com.revo.projectservice.domain.exception;

public class NoTaskFoundException extends RuntimeException{
    private static final String MESSAGE = "Error while getting task, probably task not exists in base or user are not owner!";

    public NoTaskFoundException() {
        super(MESSAGE);
    }
}

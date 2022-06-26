package com.revo.projectservice.domain.exception;

public class ProjectNotFoundException extends RuntimeException{
    private static final String MESSAGE = "Error while getting project with id %s, probably project not exists or user is not owner!";

    public ProjectNotFoundException(String id) {
        super(MESSAGE.formatted(id));
    }
}

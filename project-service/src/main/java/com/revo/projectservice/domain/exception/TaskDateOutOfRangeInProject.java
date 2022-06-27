package com.revo.projectservice.domain.exception;

public class TaskDateOutOfRangeInProject extends RuntimeException{
    private static final String MESSAGE = "Error while adding task to project, start or end date is out of range in project!";

    public TaskDateOutOfRangeInProject() {
        super(MESSAGE);
    }
}

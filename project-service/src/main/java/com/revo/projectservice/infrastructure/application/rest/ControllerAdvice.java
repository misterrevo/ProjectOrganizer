package com.revo.projectservice.infrastructure.application.rest;

import com.revo.projectservice.domain.exception.NoPermissionException;
import com.revo.projectservice.domain.exception.NoTaskFoundException;
import com.revo.projectservice.domain.exception.ProjectNotFoundException;
import com.revo.projectservice.domain.exception.TaskDateOutOfRangeInProject;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestControllerAdvice
class ControllerAdvice {
    @ExceptionHandler(NoPermissionException.class)
    @ResponseStatus(UNAUTHORIZED)
    public String handleNoPermissionException(NoPermissionException exception){
        return exception.getMessage();
    }

    @ExceptionHandler(NoTaskFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public String handleNoTaskFoundException(NoTaskFoundException exception){
        return exception.getMessage();
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public String handleProjectNotFoundException(ProjectNotFoundException exception){
        return exception.getMessage();
    }

    @ExceptionHandler(TaskDateOutOfRangeInProject.class)
    @ResponseStatus(BAD_REQUEST)
    public String handleTaskDateOutOfRangeInProject(TaskDateOutOfRangeInProject exception){
        return exception.getMessage();
    }
}

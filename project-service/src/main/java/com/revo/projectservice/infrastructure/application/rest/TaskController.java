package com.revo.projectservice.infrastructure.application.rest;

import com.revo.projectservice.domain.dto.RequestTaskDto;
import com.revo.projectservice.domain.dto.TaskDto;
import com.revo.projectservice.domain.port.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequestMapping("/tasks")
class TaskController {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String PROJECT_ID_PATH_VARIABLE = "projectId";
    private static final String TASKS_LOCATION = "/tasks";
    private static final String ID_PATH_VARIABLE = "id";

    private final TaskService taskService;

    TaskController(TaskService taskServicePort) {
        this.taskService = taskServicePort;
    }

    @PostMapping("/{projectId}")
    Mono<ResponseEntity<TaskDto>> createTaskByTokenAndProjectId(@RequestHeader(AUTHORIZATION_HEADER) String token, @PathVariable(PROJECT_ID_PATH_VARIABLE) String projectId, @RequestBody Mono<RequestTaskDto> restTaskDtoMono){
        return restTaskDtoMono
                .flatMap(requestTaskDto -> taskService.createTaskByTokenAndProjectId(token, projectId, requestTaskDto))
                .map(taskDto -> ResponseEntity.created(URI.create(TASKS_LOCATION)).body(taskDto));
    }

    @PatchMapping("/{id}")
    Mono<ResponseEntity<TaskDto>> editTaskByTokenAndId(@RequestHeader(AUTHORIZATION_HEADER) String token, @PathVariable(ID_PATH_VARIABLE) String id, @RequestBody Mono<RequestTaskDto> restTaskDtoMono){
        return restTaskDtoMono
                .flatMap(requestTaskDto -> taskService.editTaskByTokenAndId(token, id, requestTaskDto))
                .map(taskDto -> ResponseEntity.ok(taskDto));
    }

    @DeleteMapping("/{id}")
    Mono<ResponseEntity<TaskDto>> deleteTaskByTokenAndId(@RequestHeader(AUTHORIZATION_HEADER) String token, @PathVariable(ID_PATH_VARIABLE) String id){
        return taskService.deleteTaskByTokenAndId(token, id)
                .map(taskDto -> ResponseEntity.ok(taskDto));
    }
}

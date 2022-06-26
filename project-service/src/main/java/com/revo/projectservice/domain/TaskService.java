package com.revo.projectservice.domain;

import com.revo.projectservice.domain.dto.RestTaskDto;
import com.revo.projectservice.domain.dto.TaskDto;
import com.revo.projectservice.domain.port.TaskServicePort;
import reactor.core.publisher.Mono;

public class TaskService implements TaskServicePort {

    @Override
    public Mono<TaskDto> createTask(String token, RestTaskDto taskDto) {
        return null;
    }

    @Override
    public Mono<TaskDto> editTask(String token, String id, RestTaskDto taskDto) {
        return null;
    }

    @Override
    public Mono<TaskDto> deleteTask(String token, String id) {
        return null;
    }
}

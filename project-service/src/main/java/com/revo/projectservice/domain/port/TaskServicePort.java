package com.revo.projectservice.domain.port;

import com.revo.projectservice.domain.dto.RestTaskDto;
import com.revo.projectservice.domain.dto.TaskDto;
import reactor.core.publisher.Mono;

public interface TaskServicePort {

    Mono<TaskDto> createTask(String token, String projectId, RestTaskDto taskDto);
    Mono<TaskDto> editTask(String token, String id, RestTaskDto taskDto);
    Mono<TaskDto> deleteTask(String token, String id);
}

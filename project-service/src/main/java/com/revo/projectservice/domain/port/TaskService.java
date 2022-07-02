package com.revo.projectservice.domain.port;

import com.revo.projectservice.domain.dto.RequestTaskDto;
import com.revo.projectservice.domain.Task;
import reactor.core.publisher.Mono;

public interface TaskService {
    Mono<Task> createTaskByTokenAndProjectId(String token, RequestTaskDto taskDto);
    Mono<Task> editTaskByTokenAndId(String token, RequestTaskDto taskDto);
    Mono<Task> deleteTaskByTokenAndId(String token, String id);
}

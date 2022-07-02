package com.revo.projectservice.domain.port;

import com.revo.projectservice.domain.dto.RequestTaskDto;
import com.revo.projectservice.domain.dto.TaskDto;
import reactor.core.publisher.Mono;

public interface TaskService {
    Mono<TaskDto> createTaskByTokenAndProjectId(String token, RequestTaskDto taskDto);
    Mono<TaskDto> editTaskByTokenAndId(String token, RequestTaskDto taskDto);
    Mono<TaskDto> deleteTaskByTokenAndId(String token, String id);
}

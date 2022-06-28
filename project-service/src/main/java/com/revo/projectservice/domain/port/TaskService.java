package com.revo.projectservice.domain.port;

import com.revo.projectservice.domain.dto.RestTaskDto;
import com.revo.projectservice.domain.dto.TaskDto;
import reactor.core.publisher.Mono;

public interface TaskService {

    Mono<TaskDto> createTaskByTokenAndProjectId(String token, String projectId, RestTaskDto taskDto);
    Mono<TaskDto> editTaskByTokenAndId(String token, String id, RestTaskDto taskDto);
    Mono<TaskDto> deleteTaskByTokenAndId(String token, String id);
}

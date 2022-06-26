package com.revo.projectservice.domain.port;

import com.revo.projectservice.domain.dto.RestProjectDto;
import com.revo.projectservice.domain.dto.ProjectDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProjectServicePort {
    Flux<ProjectDto> getAllProjects(String token);
    Mono<ProjectDto> getProject(String token, String id);
    Mono<ProjectDto> createProject(String token, RestProjectDto projectDto);
    Mono<ProjectDto> deleteProject(String token, String id);
    Mono<ProjectDto> editProject(String token, String id, RestProjectDto projectDto);
}

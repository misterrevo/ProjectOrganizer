package com.revo.projectservice.domain.port;

import com.revo.projectservice.domain.dto.ProjectDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProjectRepositoryPort {

    Flux<ProjectDto> getAllProjects(String owner);
    Mono<ProjectDto> getProject(String id);
    Mono<ProjectDto> save(ProjectDto projectDto);
    Mono<ProjectDto> delete(String id);
    Mono<ProjectDto> editProject(String id, ProjectDto projectDto);
}

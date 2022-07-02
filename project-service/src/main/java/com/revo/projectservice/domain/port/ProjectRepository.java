package com.revo.projectservice.domain.port;

import com.revo.projectservice.domain.dto.ProjectDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProjectRepository {
    Flux<ProjectDto> getAllProjectsByOwner(String owner);
    Mono<ProjectDto> getProjectByOwner(String id, String owner);
    Mono<ProjectDto> saveProject(ProjectDto projectDto);
    Mono<ProjectDto> deleteProject(String id, String owner);
}

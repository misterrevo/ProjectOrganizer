package com.revo.projectservice.domain.port;

import com.revo.projectservice.domain.Project;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProjectRepository {
    Flux<Project> getAllProjectsByOwner(String owner);
    Mono<Project> getProjectByOwner(String id, String owner);
    Mono<Project> saveProject(Project project);
    Mono<Project> deleteProject(String id, String owner);
}

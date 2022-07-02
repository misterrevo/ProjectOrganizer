package com.revo.projectservice.domain.port;

import com.revo.projectservice.domain.dto.RequestProjectDto;
import com.revo.projectservice.domain.Project;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProjectService {
    Flux<Project> getAllProjectsByToken(String token);
    Mono<Project> getProjectByTokenAndId(String token, String id);
    Mono<Project> createProjectByToken(String token, RequestProjectDto projectDto);
    Mono<Project> deleteProjectByTokenAndId(String token, String id);
    Mono<Project> editProjectByTokenAndId(String token, RequestProjectDto projectDto);
}

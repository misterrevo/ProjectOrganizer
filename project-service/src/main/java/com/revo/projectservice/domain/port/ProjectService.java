package com.revo.projectservice.domain.port;

import com.revo.projectservice.domain.dto.RequestProjectDto;
import com.revo.projectservice.domain.dto.ProjectDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProjectService {
    Flux<ProjectDto> getAllProjectsByToken(String token);
    Mono<ProjectDto> getProjectByTokenAndId(String token, String id);
    Mono<ProjectDto> createProjectByToken(String token, RequestProjectDto projectDto);
    Mono<ProjectDto> deleteProjectByTokenAndId(String token, String id);
    Mono<ProjectDto> editProjectByTokenAndId(String token, String id, RequestProjectDto projectDto);
}

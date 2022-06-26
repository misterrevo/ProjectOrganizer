package com.revo.projectservice.infrastructure.application.rest;

import com.revo.projectservice.domain.dto.ProjectDto;
import com.revo.projectservice.domain.dto.RestProjectDto;
import com.revo.projectservice.domain.port.ProjectServicePort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/projects")
class ProjectController {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String ID_PATH_VARIABLE = "id";
    private static final String PROJECTS_LOCATION = "/projects";

    private final ProjectServicePort projectServicePort;

    ProjectController(ProjectServicePort projectServicePort) {
        this.projectServicePort = projectServicePort;
    }

    @GetMapping
    ResponseEntity<Flux<ProjectDto>> getAllProjects(@RequestHeader(AUTHORIZATION_HEADER) String token){
        return ResponseEntity.ok(Flux.just(token)
                .flatMap(target -> projectServicePort.getAllProjects(token)));
    }

    @GetMapping("/{id}")
    Mono<ResponseEntity<ProjectDto>> getProject(@RequestHeader(AUTHORIZATION_HEADER) String token, @PathVariable(ID_PATH_VARIABLE) String id){
        return projectServicePort.getProject(token, id)
                .map(projectDto -> ResponseEntity.ok(projectDto));
    }

    @PostMapping
    Mono<ResponseEntity<ProjectDto>> createProject(@RequestHeader(AUTHORIZATION_HEADER) String token, @RequestBody Mono<RestProjectDto> projectDtoMono){
        return projectDtoMono
                .flatMap(dto -> projectServicePort.createProject(token, dto))
                .map(project -> ResponseEntity.created(URI.create(PROJECTS_LOCATION)).body(project));
    }

    @DeleteMapping("/{id}")
    Mono<ResponseEntity<ProjectDto>> deleteProject(@RequestHeader(AUTHORIZATION_HEADER) String token, @PathVariable(ID_PATH_VARIABLE) String id){
        return projectServicePort.deleteProject(token, id)
                .map(projectDto -> ResponseEntity.ok(projectDto));
    }

    @PatchMapping("/{id}")
    Mono<ResponseEntity<ProjectDto>> editProject(@RequestHeader(AUTHORIZATION_HEADER) String token, @PathVariable(ID_PATH_VARIABLE) String id, @RequestBody Mono<RestProjectDto> restProjectDtoMono){
        return restProjectDtoMono
                .flatMap(dto -> projectServicePort.editProject(token, id, dto))
                .map(projectDto -> ResponseEntity.ok(projectDto));
    }
}

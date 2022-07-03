package com.revo.projectservice.infrastructure.application.rest;

import com.revo.projectservice.domain.Project;
import com.revo.projectservice.domain.dto.RequestProjectDto;
import com.revo.projectservice.domain.port.ProjectService;
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

@RestController
@RequestMapping("/projects")
class ProjectController {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String ID_PATH_VARIABLE = "id";
    private static final String PROJECTS_LOCATION = "/projects";

    private final ProjectService projectService;

    ProjectController(ProjectService projectServicePort) {
        this.projectService = projectServicePort;
    }

    @GetMapping
    ResponseEntity<Flux<Project>> getAllProjectsByToken(@RequestHeader(AUTHORIZATION_HEADER) String token){
        return ResponseEntity.ok(Flux.just(token)
                .flatMap(projectService::getAllProjectsByToken));
    }

    @GetMapping("/{id}")
    Mono<ResponseEntity<Project>> getProjectByTokenAndId(@RequestHeader(AUTHORIZATION_HEADER) String token, @PathVariable(ID_PATH_VARIABLE) String id){
        return projectService.getProjectByTokenAndId(token, id)
                .map(ResponseEntity::ok);
    }

    @PostMapping
    Mono<ResponseEntity<Project>> createProjectByToken(@RequestHeader(AUTHORIZATION_HEADER) String token, @RequestBody Mono<RequestProjectDto> requestProjectDtoMono){
        return requestProjectDtoMono
                .flatMap(requestProjectDto -> projectService.createProjectByToken(token, requestProjectDto))
                .map(projectDto -> ResponseEntity.created(URI.create(PROJECTS_LOCATION)).body(projectDto));
    }

    @DeleteMapping("/{id}")
    Mono<ResponseEntity<Project>> deleteProjectByTokenAndId(@RequestHeader(AUTHORIZATION_HEADER) String token, @PathVariable(ID_PATH_VARIABLE) String id){
        return projectService.deleteProjectByTokenAndId(token, id)
                .map(ResponseEntity::ok);
    }

    @PatchMapping("/{id}")
    Mono<ResponseEntity<Project>> editProjectByTokenAndId(@RequestHeader(AUTHORIZATION_HEADER) String token, @PathVariable(ID_PATH_VARIABLE) String id, @RequestBody Mono<RequestProjectDto> restProjectDtoMono){
        return restProjectDtoMono
                .flatMap(requestProjectDto -> {
                    requestProjectDto.id = id;
                    return projectService.editProjectByTokenAndId(token, requestProjectDto);
                })
                .map(ResponseEntity::ok);
    }
}

package com.revo.projectservice.infrastructure.application.rest;

import com.revo.projectservice.domain.dto.ProjectDto;
import com.revo.projectservice.domain.dto.RestProjectDto;
import com.revo.projectservice.domain.port.ProjectServicePort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/projects")
class ProjectController {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private final ProjectServicePort projectServicePort;

    ProjectController(ProjectServicePort projectServicePort) {
        this.projectServicePort = projectServicePort;
    }

    @GetMapping
    ResponseEntity<Flux<ProjectDto>> getAllProjects(@RequestHeader(AUTHORIZATION_HEADER) String token){
        return ResponseEntity.ok(Flux.just(token)
                .flatMap(target -> projectServicePort.getAllProjects(token)));
    }
}

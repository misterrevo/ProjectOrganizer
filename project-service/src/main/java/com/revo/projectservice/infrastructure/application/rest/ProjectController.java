package com.revo.projectservice.infrastructure.application.rest;

import com.revo.projectservice.domain.port.ProjectServicePort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
class ProjectController {

    private final ProjectServicePort projectServicePort;

    ProjectController(ProjectServicePort projectServicePort) {
        this.projectServicePort = projectServicePort;
    }

    @GetMapping("/test/{token}")
    void test(@PathVariable("token") String token){
        projectServicePort.getAllProjects(token);
    }
}

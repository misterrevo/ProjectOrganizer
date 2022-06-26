package com.revo.projectservice.domain;

import com.revo.projectservice.domain.dto.ProjectDto;
import com.revo.projectservice.domain.dto.RestProjectDto;
import com.revo.projectservice.domain.exception.NoPermissionException;
import com.revo.projectservice.domain.exception.ProjectNotFoundException;
import com.revo.projectservice.domain.port.ProjectRepositoryPort;
import com.revo.projectservice.domain.port.ProjectServicePort;
import com.revo.projectservice.domain.vo.UserVO;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.function.Function;

public class ProjectService implements ProjectServicePort {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String GATEWAY_HOST = "http://localhost:8080";
    private final ProjectRepositoryPort projectRepositoryPort;
    private static final String TRANSLATE_TOKEN_PATH = "/authorize";

    public ProjectService(ProjectRepositoryPort projectRepositoryPort) {
        this.projectRepositoryPort = projectRepositoryPort;
    }

    @Override
    public Flux<ProjectDto> getAllProjects(String token) {
        return getUser(token)
                .bodyToFlux(UserVO.class)
                .flatMap(user -> projectRepositoryPort.getAllProjects(user.getUsername()))
                .switchIfEmpty(Flux.error(new NoPermissionException()));
    }

    private WebClient.ResponseSpec getUser(String token) {
        return WebClient.create(GATEWAY_HOST)
                .post()
                .uri(TRANSLATE_TOKEN_PATH)
                .header(AUTHORIZATION_HEADER, token)
                .retrieve();
    }

    @Override
    public Mono<ProjectDto> getProject(String token, String id) {
        return getUser(token)
                .bodyToMono(UserVO.class)
                .flatMap(user -> projectRepositoryPort.getProject(id, user.getUsername()))
                .switchIfEmpty(Mono.error(new ProjectNotFoundException(id)));
    }

    @Override
    public Mono<ProjectDto> createProject(String token, RestProjectDto projectDto) {
        return getUser(token)
                .bodyToMono(UserVO.class)
                .flatMap(user -> projectRepositoryPort.save(Mapper.fromRest(projectDto)));
    }

    @Override
    public Mono<ProjectDto> deleteProject(String token, String id) {
        return getUser(token)
                .bodyToMono(UserVO.class)
                .flatMap(user -> projectRepositoryPort.delete(id, user.getUsername()));
    }

    @Override
    public Mono<ProjectDto> editProject(String token, String id, RestProjectDto projectDto) {
        return getUser(token)
                .bodyToMono(UserVO.class)
                .flatMap(user -> {
                    ProjectDto dto = Mapper.fromRest(projectDto);
                    dto.setId(id);
                    return projectRepositoryPort.save(dto);
                });
    }
}

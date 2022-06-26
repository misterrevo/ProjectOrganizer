package com.revo.projectservice.domain;

import com.revo.projectservice.domain.dto.ProjectDto;
import com.revo.projectservice.domain.dto.RestProjectDto;
import com.revo.projectservice.domain.port.ProjectRepositoryPort;
import com.revo.projectservice.domain.port.ProjectServicePort;
import com.revo.projectservice.domain.vo.UserVO;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
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
        Mono<UserVO> userVOMono = getUser(token);
        return null;
    }

    private Mono<UserVO> getUser(String token) {
        return WebClient.create(GATEWAY_HOST)
                .post()
                .uri(TRANSLATE_TOKEN_PATH)
                .header(AUTHORIZATION_HEADER, token)
                .retrieve()
                .bodyToMono(UserVO.class);
    }

    @Override
    public Mono<ProjectDto> getProject(String token, String id) {
        return null;
    }

    @Override
    public Mono<ProjectDto> createProject(String token, RestProjectDto projectDto) {
        return null;
    }

    @Override
    public Mono<ProjectDto> deleteProject(String token, String id) {
        return null;
    }

    @Override
    public Mono<ProjectDto> editProject(String token, String id, RestProjectDto projectDto) {
        return null;
    }
}

package com.revo.projectservice.domain;

import com.revo.projectservice.domain.dto.ProjectDto;
import com.revo.projectservice.domain.dto.RequestProjectDto;
import com.revo.projectservice.domain.dto.RequestTaskDto;
import com.revo.projectservice.domain.dto.TaskDto;
import com.revo.projectservice.domain.exception.NoPermissionException;
import com.revo.projectservice.domain.exception.NoTaskFoundException;
import com.revo.projectservice.domain.exception.ProjectNotFoundException;
import com.revo.projectservice.domain.exception.TaskDateOutOfRangeInProject;
import com.revo.projectservice.domain.port.ProjectRepository;
import com.revo.projectservice.domain.port.ProjectService;
import com.revo.projectservice.domain.port.TaskService;
import com.revo.projectservice.domain.vo.UserVO;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

public class DomainServiceImp implements ProjectService, TaskService {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String GATEWAY_HOST = "http://localhost:8080";
    private final ProjectRepository projectRepositoryPort;
    private static final String TRANSLATE_TOKEN_PATH = "/authorize";

    public DomainServiceImp(ProjectRepository projectRepositoryPort) {
        this.projectRepositoryPort = projectRepositoryPort;
    }

    @Override
    public Flux<ProjectDto> getAllProjectsByToken(String token) {
        return getUserFromAuthServiceAsResponse(token)
                .bodyToFlux(UserVO.class)
                .flatMap(user -> projectRepositoryPort.getAllProjectsByOwner(user.getUsername()));
    }

    private WebClient.ResponseSpec getUserFromAuthServiceAsResponse(String token) {
        return WebClient.create(GATEWAY_HOST)
                .post()
                .uri(TRANSLATE_TOKEN_PATH)
                .header(AUTHORIZATION_HEADER, token)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(new NoPermissionException()));
    }

    @Override
    public Mono<ProjectDto> getProjectByTokenAndId(String token, String id) {
        return getUserFromAuthServiceAsResponse(token)
                .bodyToMono(UserVO.class)
                .flatMap(user -> projectRepositoryPort.getProjectByOwner(id, user.getUsername()))
                .switchIfEmpty(Mono.error(new ProjectNotFoundException(id)));
    }

    @Override
    public Mono<ProjectDto> createProjectByToken(String token, RequestProjectDto requestProjectDto) {
        return getUserFromAuthServiceAsResponse(token)
                .bodyToMono(UserVO.class)
                .flatMap(user -> {
                    ProjectDto projectDto = Mapper.mapProjectFromRestDto(requestProjectDto);
                    projectDto.setOwner(user.getUsername());
                    return projectRepositoryPort.saveProject(projectDto);
                });
    }

    @Override
    public Mono<ProjectDto> deleteProjectByTokenAndId(String token, String id) {
        return getUserFromAuthServiceAsResponse(token)
                .bodyToMono(UserVO.class)
                .flatMap(user -> projectRepositoryPort.deleteProject(id, user.getUsername()));
    }

    @Override
    public Mono<ProjectDto> editProjectByTokenAndId(String token, String id, RequestProjectDto requestProjectDto) {
        return getUserFromAuthServiceAsResponse(token)
                .bodyToMono(UserVO.class)
                .flatMap(user -> {
                    ProjectDto projectDto = Mapper.mapProjectFromRestDto(requestProjectDto);
                    projectDto.setId(id);
                    return projectRepositoryPort.saveProject(projectDto);
                });
    }

    @Override
    public Mono<TaskDto> createTaskByTokenAndProjectId(String token, String projectId, RequestTaskDto requestTaskDto) {
        return getUserFromAuthServiceAsResponse(token)
                .bodyToMono(UserVO.class)
                .flatMap(user -> {
                    return projectRepositoryPort.getProjectByOwner(projectId, user.getUsername()).flatMap(projectDto -> {
                        if(projectDto.getEndDate().isBefore(requestTaskDto.getEndDate()) || projectDto.getStartDate().isAfter(requestTaskDto.getStartDate())){
                            return Mono.error(new TaskDateOutOfRangeInProject());
                        }
                        TaskDto taskDto = Mapper.mapTaskFromRestDto(requestTaskDto);
                        taskDto.setId(generateId());
                        projectDto.getTasks().add(taskDto);
                        projectRepositoryPort.saveProject(projectDto)
                                .subscribe();
                        return Mono.just(taskDto);
                    });
                });
    }

    private String generateId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public Mono<TaskDto> editTaskByTokenAndId(String token, String id, RequestTaskDto requestTaskDto) {
        return getUserFromAuthServiceAsResponse(token)
                .bodyToMono(UserVO.class)
                .flatMap(user -> {
                    return Mono.from(projectRepositoryPort.getAllProjectsByOwner(user.getUsername()).flatMap(projectDto -> {
                        if(projectDto.getEndDate().isBefore(requestTaskDto.getEndDate()) || projectDto.getStartDate().isAfter(requestTaskDto.getStartDate())){
                            return Mono.error(new TaskDateOutOfRangeInProject());
                        }
                        for (TaskDto taskDto : projectDto.getTasks()) {
                            if (Objects.equals(taskDto.getId(), id)) {
                                updateTask(taskDto, requestTaskDto);
                                projectRepositoryPort.saveProject(projectDto)
                                        .subscribe();
                                return Mono.just(taskDto);
                            }
                        }
                        return Mono.error(new NoTaskFoundException());
                    }));
                });
    }

    private void updateTask(TaskDto taskDto, RequestTaskDto requestTaskDto) {
        taskDto.setName(requestTaskDto.getName());
        taskDto.setDescription(requestTaskDto.getDescription());
        taskDto.setStartDate(requestTaskDto.getStartDate());
        taskDto.setEndDate(requestTaskDto.getEndDate());
    }

    @Override
    public Mono<TaskDto> deleteTaskByTokenAndId(String token, String id) {
        return getUserFromAuthServiceAsResponse(token)
                .bodyToMono(UserVO.class)
                .flatMap(user -> {
                    return Mono.from(projectRepositoryPort.getAllProjectsByOwner(user.getUsername()).flatMap(projectDto -> {
                        for (TaskDto taskDto : projectDto.getTasks()) {
                            if (Objects.equals(taskDto.getId(), id)) {
                                projectRepositoryPort.deleteProject(id, user.getUsername());
                                return Mono.just(taskDto);
                            }
                        }
                        return Mono.error(new NoTaskFoundException());
                    }));
                });
    }
}
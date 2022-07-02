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
import com.revo.projectservice.domain.dto.AuthorizedUser;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

import static com.revo.projectservice.domain.Mapper.mapProjectDtoFromRestDto;

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
                .bodyToFlux(AuthorizedUser.class)
                .flatMap(this::getAllProjectsByOwner);
    }

    private Flux<ProjectDto> getAllProjectsByOwner(AuthorizedUser user) {
        return projectRepositoryPort.getAllProjectsByOwner(user.getUsername());
    }

    private WebClient.ResponseSpec getUserFromAuthServiceAsResponse(String token) {
        return WebClient.create(GATEWAY_HOST)
                .post()
                .uri(TRANSLATE_TOKEN_PATH)
                .header(AUTHORIZATION_HEADER, token)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, throwNoPermissionException());
    }

    private Function<ClientResponse, Mono<? extends Throwable>> throwNoPermissionException() {
        return clientResponse -> Mono.error(new NoPermissionException());
    }

    @Override
    public Mono<ProjectDto> getProjectByTokenAndId(String token, String id) {
        return getUserFromAuthServiceAsResponse(token)
                .bodyToMono(AuthorizedUser.class)
                .flatMap(user -> getProjectByOwnerAndId(id, user))
                .switchIfEmpty(getProjectNotFundExceptionError(id));
    }

    private Mono<ProjectDto> getProjectNotFundExceptionError(String id) {
        return Mono.error(new ProjectNotFoundException(id));
    }

    @Override
    public Mono<ProjectDto> createProjectByToken(String token, RequestProjectDto requestProjectDto) {
        return getUserFromAuthServiceAsResponse(token)
                .bodyToMono(AuthorizedUser.class)
                .flatMap(user -> {
                    ProjectDto projectDto = mapProjectDtoFromRestDto(requestProjectDto);
                    projectDto.setOwner(user.getUsername());
                    return saveProjectDtoMono(projectDto);
                });
    }

    private Mono<ProjectDto> saveProjectDtoMono(ProjectDto projectDto) {
        return projectRepositoryPort.saveProject(projectDto);
    }

    @Override
    public Mono<ProjectDto> deleteProjectByTokenAndId(String token, String id) {
        return getUserFromAuthServiceAsResponse(token)
                .bodyToMono(AuthorizedUser.class)
                .flatMap(user -> deleteProjectByIdAndUser(id, user));
    }

    private Mono<ProjectDto> deleteProjectByIdAndUser(String id, AuthorizedUser user) {
        return deleteProjectByIdAndOwner(id, user);
    }

    @Override
    public Mono<ProjectDto> editProjectByTokenAndId(String token, RequestProjectDto requestProjectDto) {
        return getUserFromAuthServiceAsResponse(token)
                .bodyToMono(AuthorizedUser.class)
                .flatMap(user -> {
                    ProjectDto projectDto = mapProjectDtoFromRestDto(requestProjectDto);
                    return saveProjectDtoMono(projectDto);
                });
    }

    @Override
    public Mono<TaskDto> createTaskByTokenAndProjectId(String token, RequestTaskDto requestTaskDto) {
        return getUserFromAuthServiceAsResponse(token)
                .bodyToMono(AuthorizedUser.class)
                .flatMap(user -> {
                    return getProjectByOwnerAndId(requestTaskDto.getProjectId(), user)
                            .flatMap(projectDto -> {
                                if (isNotInProjectTimestamp(requestTaskDto, projectDto)) {
                                    return getTaskDateOutOfRangeInProjectError();
                                }
                                TaskDto taskDto = Mapper.mapTaskDtoFromRestDto(requestTaskDto);
                                taskDto.setId(generateId());
                                projectDto.getTasks().add(taskDto);
                                saveProjectDtoMono(projectDto)
                                        .subscribe();
                                return Mono.just(taskDto);
                            });
                });
    }

    private Mono<TaskDto> getTaskDateOutOfRangeInProjectError() {
        return Mono.error(new TaskDateOutOfRangeInProject());
    }

    private boolean isNotInProjectTimestamp(RequestTaskDto requestTaskDto, ProjectDto projectDto) {
        return projectDto.getEndDate().isBefore(requestTaskDto.getEndDate()) || projectDto.getStartDate().isAfter(requestTaskDto.getStartDate());
    }

    private Mono<ProjectDto> getProjectByOwnerAndId(String projectId, AuthorizedUser user) {
        return projectRepositoryPort.getProjectByOwner(projectId, user.getUsername());
    }

    private String generateId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public Mono<TaskDto> editTaskByTokenAndId(String token, RequestTaskDto requestTaskDto) {
        return getUserFromAuthServiceAsResponse(token)
                .bodyToMono(AuthorizedUser.class)
                .flatMap(user -> {
                    return Mono.from(getAllProjectsByOwner(user.getUsername()).flatMap(projectDto -> {
                        if (isNotInProjectTimestamp(requestTaskDto, projectDto)) {
                            return getTaskDateOutOfRangeInProjectError();
                        }
                        return checkTaskIsInProjectAndUpdateOrGetError(requestTaskDto.getId(), requestTaskDto, projectDto);
                    }));
                });
    }

    private Mono<TaskDto> checkTaskIsInProjectAndUpdateOrGetError(String id, RequestTaskDto requestTaskDto, ProjectDto projectDto) {
        for (TaskDto taskDto : projectDto.getTasks()) {
            if (isCurrentTask(id, taskDto)) {
                return updateTaskInProjectAndReturn(requestTaskDto, projectDto, taskDto);
            }
        }
        return getNoTaskFoundError();
    }

    private Mono<TaskDto> updateTaskInProjectAndReturn(RequestTaskDto requestTaskDto, ProjectDto projectDto, TaskDto taskDto) {
        updateTask(taskDto, requestTaskDto);
        saveProjectDtoMono(projectDto).subscribe();
        return Mono.just(taskDto);
    }

    private Mono<TaskDto> getNoTaskFoundError() {
        return Mono.error(new NoTaskFoundException());
    }

    private boolean isCurrentTask(String id, TaskDto taskDto) {
        return Objects.equals(taskDto.getId(), id);
    }

    private Flux<ProjectDto> getAllProjectsByOwner(String username) {
        return projectRepositoryPort.getAllProjectsByOwner(username);
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
                .bodyToMono(AuthorizedUser.class)
                .flatMap(user -> {
                    return Mono.from(getAllProjectsByOwner(user.getUsername())).flatMap(projectDto -> {
                        for (TaskDto taskDto : projectDto.getTasks()) {
                            if (Objects.equals(taskDto.getId(), id)) {
                                deleteProjectByIdAndOwner(id, user);
                                return Mono.just(taskDto);
                            }
                        }
                        return getNoTaskFoundError();
                    });
                });
    }

    private Mono<ProjectDto> deleteProjectByIdAndOwner(String id, AuthorizedUser user) {
        return projectRepositoryPort.deleteProject(id, user.getUsername());
    }
}

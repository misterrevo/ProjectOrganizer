package com.revo.projectservice.domain;

import com.revo.projectservice.domain.dto.AuthorizedUser;
import com.revo.projectservice.domain.dto.RequestProjectDto;
import com.revo.projectservice.domain.dto.RequestTaskDto;
import com.revo.projectservice.domain.exception.NoPermissionException;
import com.revo.projectservice.domain.exception.NoTaskFoundException;
import com.revo.projectservice.domain.exception.ProjectNotFoundException;
import com.revo.projectservice.domain.exception.TaskDateOutOfRangeInProject;
import com.revo.projectservice.domain.port.ProjectRepository;
import com.revo.projectservice.domain.port.ProjectService;
import com.revo.projectservice.domain.port.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

import static com.revo.projectservice.domain.Mapper.mapProjectFromRestDto;

public class DomainServiceImp implements ProjectService, TaskService {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String GATEWAY_HOST = "http://localhost:8080";
    private final ProjectRepository projectRepositoryPort;
    private static final String TRANSLATE_TOKEN_PATH = "/authorize";

    public DomainServiceImp(ProjectRepository projectRepositoryPort) {
        this.projectRepositoryPort = projectRepositoryPort;
    }

    @Override
    public Flux<Project> getAllProjectsByToken(String token) {
        return getAuthorizedUserFluxFromToken(token)
                .flatMap(this::getAllProjectsByOwner);
    }

    private Flux<AuthorizedUser> getAuthorizedUserFluxFromToken(String token) {
        return getUserFromAuthServiceAsResponse(token)
                .bodyToFlux(AuthorizedUser.class);
    }

    private Flux<Project> getAllProjectsByOwner(AuthorizedUser user) {
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
    public Mono<Project> getProjectByTokenAndId(String token, String id) {
        return getAuthorizedUserMonoFromToken(token)
                .flatMap(user -> getProjectByOwnerAndId(id, user))
                .switchIfEmpty(getProjectNotFundExceptionError(id));
    }

    private Mono<AuthorizedUser> getAuthorizedUserMonoFromToken(String token) {
        return getUserFromAuthServiceAsResponse(token)
                .bodyToMono(AuthorizedUser.class);
    }

    private Mono<Project> getProjectNotFundExceptionError(String id) {
        return Mono.error(new ProjectNotFoundException(id));
    }

    @Override
    public Mono<Project> createProjectByToken(String token, RequestProjectDto requestProjectDto) {
        return getAuthorizedUserMonoFromToken(token)
                .flatMap(user -> {
                    Project project = mapProjectFromRestDto(requestProjectDto);
                    project.setOwner(user.getUsername());
                    return saveProjectDtoMono(project);
                });
    }

    private Mono<Project> saveProjectDtoMono(Project project) {
        return projectRepositoryPort.saveProject(project);
    }

    @Override
    public Mono<Project> deleteProjectByTokenAndId(String token, String id) {
        return getAuthorizedUserMonoFromToken(token)
                .flatMap(user -> deleteProjectByIdAndUser(id, user));
    }

    private Mono<Project> deleteProjectByIdAndUser(String id, AuthorizedUser user) {
        return deleteProjectByIdAndOwner(id, user);
    }

    @Override
    public Mono<Project> editProjectByTokenAndId(String token, RequestProjectDto requestProjectDto) {
        return getAuthorizedUserMonoFromToken(token)
                .flatMap(user -> {
                    Project project = Mapper.mapProjectFromRestDto(requestProjectDto);
                    return saveProjectDtoMono(project);
                });
    }

    @Override
    public Mono<Task> createTaskByTokenAndProjectId(String token, RequestTaskDto requestTaskDto) {
        return getAuthorizedUserMonoFromToken(token)
                .flatMap(user -> {
                    return getProjectByOwnerAndId(requestTaskDto.getProjectId(), user)
                            .flatMap(projectDto -> {
                                if (isNotInProjectTimestamp(requestTaskDto, projectDto)) {
                                    return getTaskDateOutOfRangeInProjectError();
                                }
                                Task task = Mapper.mapTaskFromRestDto(requestTaskDto);
                                task.setId(generateId());
                                List<Task> taskList = projectDto.getTasks();
                                taskList.add(task);
                                saveProjectDtoMono(projectDto)
                                        .subscribe();
                                return Mono.just(task);
                            });
                });
    }

    private Mono<Task> getTaskDateOutOfRangeInProjectError() {
        return Mono.error(new TaskDateOutOfRangeInProject());
    }

    private boolean isNotInProjectTimestamp(RequestTaskDto requestTaskDto, Project project) {
        LocalDateTime endDate = project.getEndDate();
        LocalDateTime startDate = project.getStartDate();
        return endDate.isBefore(requestTaskDto.getEndDate()) || startDate.isAfter(requestTaskDto.getStartDate());
    }

    private Mono<Project> getProjectByOwnerAndId(String projectId, AuthorizedUser user) {
        return projectRepositoryPort.getProjectByOwner(projectId, user.getUsername());
    }

    private String generateId() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    @Override
    public Mono<Task> editTaskByTokenAndId(String token, RequestTaskDto requestTaskDto) {
        return getAuthorizedUserMonoFromToken(token)
                .flatMap(user -> {
                    return Mono.from(getAllProjectsByOwner(user.getUsername())
                            .flatMap(projectDto -> {
                                if (isNotInProjectTimestamp(requestTaskDto, projectDto)) {
                                    return getTaskDateOutOfRangeInProjectError();
                                }
                                return checkTaskIsInProjectAndUpdateOrGetError(requestTaskDto.getId(), requestTaskDto, projectDto);
                            }));
                });
    }

    private Mono<Task> checkTaskIsInProjectAndUpdateOrGetError(String id, RequestTaskDto requestTaskDto, Project project) {
        for (Task task : project.getTasks()) {
            if (isCurrentTask(id, task)) {
                return updateTaskInProjectAndReturn(requestTaskDto, project, task);
            }
        }
        return getNoTaskFoundError();
    }

    private Mono<Task> updateTaskInProjectAndReturn(RequestTaskDto requestTaskDto, Project project, Task task) {
        updateTask(task, requestTaskDto);
        saveProjectDtoMono(project).subscribe();
        return Mono.just(task);
    }

    private Mono<Task> getNoTaskFoundError() {
        return Mono.error(new NoTaskFoundException());
    }

    private boolean isCurrentTask(String id, Task task) {
        return Objects.equals(task.getId(), id);
    }

    private Flux<Project> getAllProjectsByOwner(String username) {
        return projectRepositoryPort.getAllProjectsByOwner(username);
    }

    private void updateTask(Task task, RequestTaskDto requestTaskDto) {
        task.setName(requestTaskDto.getName());
        task.setDescription(requestTaskDto.getDescription());
        task.setStartDate(requestTaskDto.getStartDate());
        task.setEndDate(requestTaskDto.getEndDate());
    }

    @Override
    public Mono<Task> deleteTaskByTokenAndId(String token, String id) {
        return getAuthorizedUserMonoFromToken(token)
                .flatMap(user -> {
                    return Mono.from(getAllProjectsByOwner(user.getUsername())).flatMap(projectDto -> {
                        for (Task task : projectDto.getTasks()) {
                            if (Objects.equals(task.getId(), id)) {
                                deleteProjectByIdAndOwner(id, user);
                                return Mono.just(task);
                            }
                        }
                        return getNoTaskFoundError();
                    });
                });
    }

    private Mono<Project> deleteProjectByIdAndOwner(String id, AuthorizedUser user) {
        return projectRepositoryPort.deleteProject(id, user.getUsername());
    }
}

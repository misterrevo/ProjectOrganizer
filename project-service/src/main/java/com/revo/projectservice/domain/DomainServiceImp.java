package com.revo.projectservice.domain;

import com.revo.projectservice.domain.dto.AuthorizedUser;
import com.revo.projectservice.domain.dto.RequestProjectDto;
import com.revo.projectservice.domain.dto.RequestTaskDto;
import com.revo.projectservice.domain.exception.NoPermissionException;
import com.revo.projectservice.domain.exception.NoTaskFoundException;
import com.revo.projectservice.domain.exception.ProjectNotFoundException;
import com.revo.projectservice.domain.exception.TaskDateOutOfRangeInProject;
import com.revo.projectservice.domain.port.AuthService;
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
    private final ProjectRepository projectRepositoryPort;
    private final AuthService authService;

    public DomainServiceImp(ProjectRepository projectRepositoryPort, AuthService authService) {
        this.projectRepositoryPort = projectRepositoryPort;
        this.authService = authService;
    }

    @Override
    public Flux<Project> getAllProjectsByToken(String token) {
        return getAuthorizedUserFluxFromToken(token)
                .flatMap(this::getAllProjectsByOwner);
    }

    private Flux<AuthorizedUser> getAuthorizedUserFluxFromToken(String token) {
        return authService.getAuthorizedUserFluxFromToken(token);
    }

    private Flux<Project> getAllProjectsByOwner(AuthorizedUser user) {
        return projectRepositoryPort.getAllProjectsByOwner(user.username);
    }

    @Override
    public Mono<Project> getProjectByTokenAndId(String token, String id) {
        return getAuthorizedUserMonoFromToken(token)
                .flatMap(user -> getProjectByOwnerAndId(id, user))
                .switchIfEmpty(getProjectNotFundExceptionError(id));
    }

    private Mono<AuthorizedUser> getAuthorizedUserMonoFromToken(String token) {
        return authService.getAuthorizedUserMonoFromToken(token);
    }

    private Mono<Project> getProjectNotFundExceptionError(String id) {
        return Mono.error(new ProjectNotFoundException(id));
    }

    @Override
    public Mono<Project> createProjectByToken(String token, RequestProjectDto requestProjectDto) {
        return getAuthorizedUserMonoFromToken(token)
                .flatMap(user -> updateProjectMono(mapProjectFromRestDto(requestProjectDto), user));
    }

    private Mono<Project> updateProjectMono(Project requestProjectDto, AuthorizedUser user) {
        Project project = requestProjectDto;
        project.setOwner(user.username);
        return saveProjectDtoMono(project);
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
                .flatMap(user -> updateProjectMono(Mapper.mapProjectFromRestDto(requestProjectDto), user));
    }

    @Override
    public Mono<Task> createTaskByTokenAndProjectId(String token, RequestTaskDto requestTaskDto) {
        return getAuthorizedUserMonoFromToken(token)
                .flatMap(user -> {
                    return getProjectByOwnerAndId(requestTaskDto.projectId, user)
                            .flatMap(projectDto -> {
                                if (isNotInProjectTimestamp(requestTaskDto, projectDto)) {
                                    return getTaskDateOutOfRangeInProjectError();
                                }
                                return updateProjectAndGetCreatedTask(requestTaskDto, projectDto);
                            });
                });
    }

    private Mono<Task> updateProjectAndGetCreatedTask(RequestTaskDto requestTaskDto, Project projectDto) {
        Task task = Mapper.mapTaskFromRestDto(requestTaskDto);
        task.setId(generateId());
        List<Task> taskList = projectDto.getTasks();
        taskList.add(task);
        saveProjectDtoMono(projectDto)
                .subscribe();
        return Mono.just(task);
    }

    private Mono<Task> getTaskDateOutOfRangeInProjectError() {
        return Mono.error(new TaskDateOutOfRangeInProject());
    }

    private boolean isNotInProjectTimestamp(RequestTaskDto requestTaskDto, Project project) {
        LocalDateTime endDate = project.getEndDate();
        LocalDateTime startDate = project.getStartDate();
        return endDate.isBefore(requestTaskDto.endDate) || startDate.isAfter(requestTaskDto.startDate);
    }

    private Mono<Project> getProjectByOwnerAndId(String projectId, AuthorizedUser user) {
        return projectRepositoryPort.getProjectByOwner(projectId, user.username);
    }

    private String generateId() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    @Override
    public Mono<Task> editTaskByTokenAndId(String token, RequestTaskDto requestTaskDto) {
        return getAuthorizedUserMonoFromToken(token)
                .flatMap(user -> {
                    return getProjectMonoByTaskId(requestTaskDto.id, user)
                            .flatMap(project -> {
                                if (isNotInProjectTimestamp(requestTaskDto, project)) {
                                    return getTaskDateOutOfRangeInProjectError();
                                }
                                return checkTaskIsInProjectAndUpdateOrGetError(requestTaskDto, project);
                            });
                });
    }

    private Mono<Project> getProjectMonoByTaskId(String taskId, AuthorizedUser user) {
        return Mono.from(getAllProjectsByOwner(user.username)
                .filter(project -> {
                    for (Task task : project.getTasks()) {
                        if (isCurrentTask(taskId, task)) {
                            return true;
                        }
                    }
                    return false;
                }));
    }

    private Mono<Task> checkTaskIsInProjectAndUpdateOrGetError(RequestTaskDto requestTaskDto, Project project) {
        for (Task task : project.getTasks()) {
            if (isCurrentTask(requestTaskDto.id, task)) {
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
        task.setName(requestTaskDto.name);
        task.setDescription(requestTaskDto.description);
        task.setStartDate(requestTaskDto.startDate);
        task.setEndDate(requestTaskDto.endDate);
    }

    @Override
    public Mono<Task> deleteTaskByTokenAndId(String token, String id) {
        return getAuthorizedUserMonoFromToken(token)
                .flatMap(user -> {
                    return getProjectMonoByTaskId(id, user).flatMap(project -> {
                        for (Task task : project.getTasks()) {
                            if (isCurrentTask(id, task)) {
                                return updateProjectAndGetDeletedTask(project, task);
                            }
                        }
                        return getNoTaskFoundError();
                    });
                });
    }

    private Mono<Task> updateProjectAndGetDeletedTask(Project project, Task task) {
        List<Task> taskList = project.getTasks();
        taskList.remove(task);
        saveProjectDtoMono(project)
                .subscribe();
        return Mono.just(task);
    }

    private Mono<Project> deleteProjectByIdAndOwner(String id, AuthorizedUser user) {
        return projectRepositoryPort.deleteProject(id, user.username);
    }
}

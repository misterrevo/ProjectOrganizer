package com.revo.projectservice.domain;

import com.revo.projectservice.domain.dto.ProjectDto;
import com.revo.projectservice.domain.dto.RestProjectDto;
import com.revo.projectservice.domain.dto.RestTaskDto;
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

public class Service implements ProjectService, TaskService {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String GATEWAY_HOST = "http://localhost:8080";
    private final ProjectRepository projectRepositoryPort;
    private static final String TRANSLATE_TOKEN_PATH = "/authorize";

    public Service(ProjectRepository projectRepositoryPort) {
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
    public Mono<ProjectDto> createProjectByToken(String token, RestProjectDto projectDto) {
        return getUserFromAuthServiceAsResponse(token)
                .bodyToMono(UserVO.class)
                .flatMap(user -> {
                    ProjectDto dto = Mapper.mapProjectFromRestDto(projectDto);
                    dto.setOwner(user.getUsername());
                    return projectRepositoryPort.saveProject(dto);
                });
    }

    @Override
    public Mono<ProjectDto> deleteProjectByTokenAndId(String token, String id) {
        return getUserFromAuthServiceAsResponse(token)
                .bodyToMono(UserVO.class)
                .flatMap(user -> projectRepositoryPort.deleteProject(id, user.getUsername()));
    }

    @Override
    public Mono<ProjectDto> editProjectByTokenAndId(String token, String id, RestProjectDto projectDto) {
        return getUserFromAuthServiceAsResponse(token)
                .bodyToMono(UserVO.class)
                .flatMap(user -> {
                    ProjectDto dto = Mapper.mapProjectFromRestDto(projectDto);
                    dto.setId(id);
                    return projectRepositoryPort.saveProject(dto);
                });
    }

    @Override
    public Mono<TaskDto> createTaskByTokenAndProjectId(String token, String projectId, RestTaskDto taskDto) {
        return getUserFromAuthServiceAsResponse(token)
                .bodyToMono(UserVO.class)
                .flatMap(user -> {
                    Mono<ProjectDto> dto = projectRepositoryPort.getProjectByOwner(projectId, user.getUsername());
                    return dto.flatMap(target -> {
                        if(target.getEndDate().isBefore(taskDto.getEndDate()) || target.getStartDate().isAfter(taskDto.getStartDate())){
                            return Mono.error(new TaskDateOutOfRangeInProject());
                        }
                        TaskDto task = Mapper.mapTaskFromRestDto(taskDto);
                        task.setId(generateId());
                        target.getTasks().add(task);
                        projectRepositoryPort.saveProject(target)
                                .subscribe();
                        return Mono.just(task);
                    });
                });
    }

    private String generateId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public Mono<TaskDto> editTaskByTokenAndId(String token, String id, RestTaskDto taskDto) {
        return getUserFromAuthServiceAsResponse(token)
                .bodyToMono(UserVO.class)
                .flatMap(user -> {
                    return Mono.from(projectRepositoryPort.getAllProjectsByOwner(user.getUsername()).flatMap(project -> {
                        if(project.getEndDate().isBefore(taskDto.getEndDate()) || project.getStartDate().isAfter(taskDto.getStartDate())){
                            return Mono.error(new TaskDateOutOfRangeInProject());
                        }
                        for (TaskDto task : project.getTasks()) {
                            if (Objects.equals(task.getId(), id)) {
                                updateTask(task, taskDto);
                                projectRepositoryPort.saveProject(project)
                                        .subscribe();
                                return Mono.just(task);
                            }
                        }
                        return Mono.error(new NoTaskFoundException());
                    }));
                });
    }

    private void updateTask(TaskDto task, RestTaskDto taskDto) {
        task.setName(taskDto.getName());
        task.setDescription(taskDto.getDescription());
        task.setStartDate(taskDto.getStartDate());
        task.setEndDate(taskDto.getEndDate());
    }

    @Override
    public Mono<TaskDto> deleteTaskByTokenAndId(String token, String id) {
        return getUserFromAuthServiceAsResponse(token)
                .bodyToMono(UserVO.class)
                .flatMap(user -> {
                    return Mono.from(projectRepositoryPort.getAllProjectsByOwner(user.getUsername()).flatMap(project -> {
                        for (TaskDto task : project.getTasks()) {
                            if (Objects.equals(task.getId(), id)) {
                                projectRepositoryPort.deleteProject(id, user.getUsername());
                                return Mono.just(task);
                            }
                        }
                        return Mono.error(new NoTaskFoundException());
                    }));
                });
    }
}

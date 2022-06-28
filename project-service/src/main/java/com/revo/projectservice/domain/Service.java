package com.revo.projectservice.domain;

import com.revo.projectservice.domain.dto.ProjectDto;
import com.revo.projectservice.domain.dto.RestProjectDto;
import com.revo.projectservice.domain.dto.RestTaskDto;
import com.revo.projectservice.domain.dto.TaskDto;
import com.revo.projectservice.domain.exception.NoPermissionException;
import com.revo.projectservice.domain.exception.NoTaskFoundException;
import com.revo.projectservice.domain.exception.ProjectNotFoundException;
import com.revo.projectservice.domain.exception.TaskDateOutOfRangeInProject;
import com.revo.projectservice.domain.port.ProjectRepositoryPort;
import com.revo.projectservice.domain.port.ProjectServicePort;
import com.revo.projectservice.domain.port.TaskServicePort;
import com.revo.projectservice.domain.vo.UserVO;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

public class Service implements ProjectServicePort, TaskServicePort {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String GATEWAY_HOST = "http://localhost:8080";
    private final ProjectRepositoryPort projectRepositoryPort;
    private static final String TRANSLATE_TOKEN_PATH = "/authorize";

    public Service(ProjectRepositoryPort projectRepositoryPort) {
        this.projectRepositoryPort = projectRepositoryPort;
    }

    @Override
    public Flux<ProjectDto> getAllProjects(String token) {
        return getUser(token)
                .bodyToFlux(UserVO.class)
                .flatMap(user -> projectRepositoryPort.getAllProjects(user.getUsername()));
    }

    private WebClient.ResponseSpec getUser(String token) {
        return WebClient.create(GATEWAY_HOST)
                .post()
                .uri(TRANSLATE_TOKEN_PATH)
                .header(AUTHORIZATION_HEADER, token)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(new NoPermissionException()));
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
                .flatMap(user -> {
                    ProjectDto dto = Mapper.fromRest(projectDto);
                    dto.setOwner(user.getUsername());
                    return projectRepositoryPort.save(dto);
                });
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

    @Override
    public Mono<TaskDto> createTask(String token, String projectId, RestTaskDto taskDto) {
        return getUser(token)
                .bodyToMono(UserVO.class)
                .flatMap(user -> {
                    Mono<ProjectDto> dto = projectRepositoryPort.getProject(projectId, user.getUsername());
                    return dto.flatMap(target -> {
                        if(target.getEndDate().isBefore(taskDto.getEndDate()) || target.getStartDate().isAfter(taskDto.getStartDate())){
                            return Mono.error(new TaskDateOutOfRangeInProject());
                        }
                        TaskDto task = Mapper.fromRest(taskDto);
                        task.setId(generateId());
                        target.getTasks().add(task);
                        projectRepositoryPort.save(target)
                                .subscribe();
                        return Mono.just(task);
                    });
                });
    }

    private String generateId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public Mono<TaskDto> editTask(String token, String id, RestTaskDto taskDto) {
        return getUser(token)
                .bodyToMono(UserVO.class)
                .flatMap(user -> {
                    return Mono.from(projectRepositoryPort.getAllProjects(user.getUsername()).flatMap(project -> {
                        if(project.getEndDate().isBefore(taskDto.getEndDate()) || project.getStartDate().isAfter(taskDto.getStartDate())){
                            return Mono.error(new TaskDateOutOfRangeInProject());
                        }
                        for (TaskDto task : project.getTasks()) {
                            if (Objects.equals(task.getId(), id)) {
                                updateTask(task, taskDto);
                                projectRepositoryPort.save(project)
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
    public Mono<TaskDto> deleteTask(String token, String id) {
        return getUser(token)
                .bodyToMono(UserVO.class)
                .flatMap(user -> {
                    return Mono.from(projectRepositoryPort.getAllProjects(user.getUsername()).flatMap(project -> {
                        for (TaskDto task : project.getTasks()) {
                            if (Objects.equals(task.getId(), id)) {
                                projectRepositoryPort.delete(id, user.getUsername());
                                return Mono.just(task);
                            }
                        }
                        return Mono.error(new NoTaskFoundException());
                    }));
                });
    }
}

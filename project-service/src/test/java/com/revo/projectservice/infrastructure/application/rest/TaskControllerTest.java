package com.revo.projectservice.infrastructure.application.rest;

import com.revo.projectservice.domain.dto.RequestTaskDto;
import com.revo.projectservice.domain.Task;
import com.revo.projectservice.domain.exception.NoPermissionException;
import com.revo.projectservice.domain.exception.TaskDateOutOfRangeInProject;
import com.revo.projectservice.domain.port.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = TaskController.class)
@RunWith(SpringRunner.class)
class TaskControllerTest {
    private static final String TASK_NAME = "Task";
    private static final String TASK_DESCRIPTION = "Description";
    private static final String TASK_END_POINT = "/tasks/1";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String EXAMPLE_TOKEN = "token";
    private static final String CONTENT_TYPE_HEADER_NAME = "Content-Type";
    private static final String CONTENT_TYPE_HEADER_VALUE = "application/json";

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private TaskService taskService;

    private final Task task = Task.Builder.aTaskDto()
            .name(TASK_NAME)
            .description(TASK_DESCRIPTION)
            .startDate(LocalDateTime.now())
            .endDate(LocalDateTime.now())
            .build();
    
    @Test
    void shouldReturn201WhileCreatingTask() {
        //given
        //when
        when(taskService.createTaskByTokenAndProjectId(anyString(), any(RequestTaskDto.class))).thenReturn(Mono.just(task));
        //then
        webTestClient
                .post()
                .uri(TASK_END_POINT)
                .bodyValue(mapOnRequestTaskDto(task))
                .header(AUTHORIZATION_HEADER, EXAMPLE_TOKEN)
                .header(CONTENT_TYPE_HEADER_NAME, CONTENT_TYPE_HEADER_VALUE)
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void shouldReturn400WhileCreatingTask(){
        //given
        //when
        when(taskService.createTaskByTokenAndProjectId(anyString(), any(RequestTaskDto.class))).thenReturn(Mono.error(new TaskDateOutOfRangeInProject()));
        //then
        webTestClient
                .post()
                .uri(TASK_END_POINT)
                .bodyValue(mapOnRequestTaskDto(task))
                .header(AUTHORIZATION_HEADER, EXAMPLE_TOKEN)
                .header(CONTENT_TYPE_HEADER_NAME, CONTENT_TYPE_HEADER_VALUE)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldReturn401WhileCreatingTask(){
        //given
        //when
        when(taskService.createTaskByTokenAndProjectId(anyString(), any(RequestTaskDto.class))).thenReturn(Mono.error(new NoPermissionException()));
        //then
        webTestClient
                .post()
                .uri(TASK_END_POINT)
                .bodyValue(mapOnRequestTaskDto(task))
                .header(AUTHORIZATION_HEADER, EXAMPLE_TOKEN)
                .header(CONTENT_TYPE_HEADER_NAME, CONTENT_TYPE_HEADER_VALUE)
                .exchange()
                .expectStatus().isUnauthorized();
    }
    @Test
    void shouldReturn200WhileEditingTask() {
        //given
        //when
        when(taskService.editTaskByTokenAndId(anyString(), any(RequestTaskDto.class))).thenReturn(Mono.just(task));
        //then
        webTestClient
                .patch()
                .uri(TASK_END_POINT)
                .bodyValue(mapOnRequestTaskDto(task))
                .header(AUTHORIZATION_HEADER, EXAMPLE_TOKEN)
                .header(CONTENT_TYPE_HEADER_NAME, CONTENT_TYPE_HEADER_VALUE)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldReturn400WhileEditingTask(){
        //given
        //when
        when(taskService.editTaskByTokenAndId(anyString(), any(RequestTaskDto.class))).thenReturn(Mono.error(new TaskDateOutOfRangeInProject()));
        //then
        webTestClient
                .patch()
                .uri(TASK_END_POINT)
                .bodyValue(mapOnRequestTaskDto(task))
                .header(AUTHORIZATION_HEADER, EXAMPLE_TOKEN)
                .header(CONTENT_TYPE_HEADER_NAME, CONTENT_TYPE_HEADER_VALUE)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldReturn401WhileEditingTask(){
        //given
        //when
        when(taskService.editTaskByTokenAndId(anyString(), any(RequestTaskDto.class))).thenReturn(Mono.error(new NoPermissionException()));
        //then
        webTestClient
                .patch()
                .uri(TASK_END_POINT)
                .bodyValue(mapOnRequestTaskDto(task))
                .header(AUTHORIZATION_HEADER, EXAMPLE_TOKEN)
                .header(CONTENT_TYPE_HEADER_NAME, CONTENT_TYPE_HEADER_VALUE)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void shouldReturn200WhileDeletingTask() {
        //given
        //when
        when(taskService.deleteTaskByTokenAndId(anyString(), anyString())).thenReturn(Mono.just(task));
        //then
        webTestClient
                .delete()
                .uri(TASK_END_POINT)
                .header(AUTHORIZATION_HEADER, EXAMPLE_TOKEN)
                .header(CONTENT_TYPE_HEADER_NAME, CONTENT_TYPE_HEADER_VALUE)
                .exchange()
                .expectStatus().isOk();
    }
    @Test
    void shouldReturn401WhileDeletingTask() {
        //given
        //when
        when(taskService.deleteTaskByTokenAndId(anyString(), anyString())).thenReturn(Mono.error(new NoPermissionException()));
        //then
        webTestClient
                .delete()
                .uri(TASK_END_POINT)
                .header(AUTHORIZATION_HEADER, EXAMPLE_TOKEN)
                .header(CONTENT_TYPE_HEADER_NAME, CONTENT_TYPE_HEADER_VALUE)
                .exchange()
                .expectStatus().isUnauthorized();
    }
    
    private RequestTaskDto mapOnRequestTaskDto(Task task){
        return new RequestTaskDto(null, task.getId(), task.getName(), task.getDescription(), task.getStartDate(), task.getEndDate());
    }
}
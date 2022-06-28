package com.revo.projectservice.infrastructure.application.rest;

import com.revo.projectservice.domain.dto.ProjectDto;
import com.revo.projectservice.domain.dto.RestProjectDto;
import com.revo.projectservice.domain.exception.NoPermissionException;
import com.revo.projectservice.domain.port.ProjectServicePort;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = ProjectController.class)
@RunWith(SpringRunner.class)
class ProjectControllerTest {

    private static final String PROJECT_NAME = "Test";
    private static final String OWNER_NAME = "Owner";
    private static final String PROJECTS_END_POINT = "/projects";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String EXAMPLE_TOKEN = "token";
    private static final String SINGLE_PROJECT_END_POINT = "/projects/1";
    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ProjectServicePort projectServicePort;

    private final ProjectDto projectDto = ProjectDto.Builder.aProjectDto()
            .name(PROJECT_NAME)
            .owner(OWNER_NAME)
            .startDate(LocalDateTime.now())
            .endDate(LocalDateTime.now())
            .build();

    @Test
    void shouldReturn200WhileGettingAllProjects() {
        //given
        //when
        when(projectServicePort.getAllProjects(anyString())).thenReturn(Flux.just(projectDto));
        //then
        webTestClient
                .get()
                .uri(PROJECTS_END_POINT)
                .header(AUTHORIZATION_HEADER, EXAMPLE_TOKEN)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldReturn401WhileGettingAllProjects(){
        //given
        //when
        when(projectServicePort.getAllProjects(anyString())).thenReturn(Flux.error(new NoPermissionException()));
        //then
        webTestClient
                .get()
                .uri(PROJECTS_END_POINT)
                .header(AUTHORIZATION_HEADER, EXAMPLE_TOKEN)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void shouldReturn200WhileGettingProject() {
        //given
        //when
        when(projectServicePort.getProject(anyString(), anyString())).thenReturn(Mono.just(projectDto));
        //then
        webTestClient
                .get()
                .uri(SINGLE_PROJECT_END_POINT)
                .header(AUTHORIZATION_HEADER, EXAMPLE_TOKEN)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldReturn401WhileGettingProject(){
        //given
        //when
        when(projectServicePort.getProject(anyString(), anyString())).thenReturn(Mono.error(new NoPermissionException()));
        //then
        webTestClient
                .get()
                .uri(SINGLE_PROJECT_END_POINT)
                .header(AUTHORIZATION_HEADER, EXAMPLE_TOKEN)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void shouldReturn201WhileCreatingProject() {
        //given
        //when
        when(projectServicePort.createProject(anyString(), any(RestProjectDto.class))).thenReturn(Mono.just(projectDto));
        //then
        webTestClient
                .post()
                .uri(PROJECTS_END_POINT)
                .header(AUTHORIZATION_HEADER, EXAMPLE_TOKEN)
                .bodyValue(projectDto)
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void shouldReturn401WhileCreatingProject(){
        //given
        //when
        when(projectServicePort.createProject(anyString(), any(RestProjectDto.class))).thenReturn(Mono.error(new NoPermissionException()));
        //then
        webTestClient
                .post()
                .uri(PROJECTS_END_POINT)
                .header(AUTHORIZATION_HEADER, EXAMPLE_TOKEN)
                .bodyValue(projectDto)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void shouldReturn200WhileDeletingProject() {
        //given
        //when
        when(projectServicePort.deleteProject(anyString(), anyString())).thenReturn(Mono.just(projectDto));
        //then
        webTestClient
                .delete()
                .uri(SINGLE_PROJECT_END_POINT)
                .header(AUTHORIZATION_HEADER, EXAMPLE_TOKEN)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldReturn401WhileDeletingProject() {
        //given
        //when
        when(projectServicePort.deleteProject(anyString(), anyString())).thenReturn(Mono.error(new NoPermissionException()));
        //then
        webTestClient
                .delete()
                .uri(SINGLE_PROJECT_END_POINT)
                .header(AUTHORIZATION_HEADER, EXAMPLE_TOKEN)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void shouldReturn200WhileEditingProject() {
        //given
        //when
        when(projectServicePort.editProject(anyString(), anyString(), any(RestProjectDto.class))).thenReturn(Mono.just(projectDto));
        //then
        webTestClient
                .patch()
                .uri(SINGLE_PROJECT_END_POINT)
                .header(AUTHORIZATION_HEADER, EXAMPLE_TOKEN)
                .bodyValue(projectDto)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldReturn401WhileEditingProject() {
        //given
        //when
        when(projectServicePort.editProject(anyString(), anyString(), any(RestProjectDto.class))).thenReturn(Mono.error(new NoPermissionException()));
        //then
        webTestClient
                .patch()
                .uri(SINGLE_PROJECT_END_POINT)
                .header(AUTHORIZATION_HEADER, EXAMPLE_TOKEN)
                .bodyValue(projectDto)
                .exchange()
                .expectStatus().isUnauthorized();
    }
}
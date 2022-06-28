package com.revo.projectservice.infrastructure.application.rest;

import com.revo.projectservice.domain.dto.ProjectDto;
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

import java.net.URI;
import java.time.LocalDateTime;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
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
    void shouldReturn200WhileGetingAllProjects() {
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
    void getProject() {
        //given
        //when
        //then
    }

    @Test
    void createProject() {
        //given
        //when
        //then
    }

    @Test
    void deleteProject() {
        //given
        //when
        //then
    }

    @Test
    void editProject() {
        //given
        //when
        //then
    }
}
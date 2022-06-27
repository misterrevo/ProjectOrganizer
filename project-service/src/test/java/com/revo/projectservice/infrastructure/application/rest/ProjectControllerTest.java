package com.revo.projectservice.infrastructure.application.rest;

import com.revo.projectservice.infrastructure.application.ProjectServiceApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebFluxTest(controllers = ProjectController.class)
@ContextConfiguration(classes = ProjectServiceApplication.class)
class ProjectControllerTest {

    private static final String PROJECTS_ENDPOINT = "/projects";

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void getAllProjects() throws Exception {
        //given
        //when
        //then
        webTestClient
                .get()
                .uri(PROJECTS_ENDPOINT)
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
package com.revo.projectservice.infrastructure.application.rest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DataMongoTest
@ExtendWith(SpringExtension.class)
class ProjectControllerTest {

    private static final String GET_ALL_END_POINT = "/projects";

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void getAllProjects() throws Exception {
        //given
        //when
        //then
        webTestClient.get()
                .uri(GET_ALL_END_POINT)
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
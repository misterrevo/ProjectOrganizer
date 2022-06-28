package com.revo.projectservice.infrastructure.application.rest;

import com.revo.projectservice.domain.port.TaskServicePort;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.*;

@WebFluxTest(controllers = TaskController.class)
@RunWith(SpringRunner.class)
class TaskControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private TaskServicePort taskServicePort;

    @Test
    void createTask() {
        //given
        //when
        //then
    }

    @Test
    void editTask() {
        //given
        //when
        //then
    }

    @Test
    void deleteTask() {
        //given
        //when
        //then
    }
}
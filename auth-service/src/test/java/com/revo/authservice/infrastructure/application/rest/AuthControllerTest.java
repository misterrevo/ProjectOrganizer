package com.revo.authservice.infrastructure.application.rest;

import com.revo.authservice.domain.UserService;
import com.revo.authservice.domain.dto.UserDto;
import com.revo.authservice.domain.exception.BadLoginException;
import com.revo.authservice.domain.port.UserServicePort;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = AuthController.class)
@RunWith(SpringRunner.class)
class AuthControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserServicePort userServicePort;

    private final UserDto userDto = new UserDto(null, "test", "test", "test@email.pl");

    @Test
    void shouldReturn200WhileLoggingUser() {
        //given
        //when
        when(userServicePort.loginUser(any(UserDto.class))).thenReturn(Mono.just(userDto));
        //then
        webTestClient
                .post()
                .uri("/login")
                .bodyValue(userDto)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldReturn401WhileLoggingUser(){
        //given
        //when
        when(userServicePort.loginUser(any(UserDto.class))).thenReturn(Mono.error(new BadLoginException()));
        //then
        webTestClient
                .post()
                .uri("/login")
                .bodyValue(userDto)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void registerUser() {
        //given
        //when
        //then
    }

    @Test
    void translateTokenOnUser() {
        //given
        //when
        //then
    }
}
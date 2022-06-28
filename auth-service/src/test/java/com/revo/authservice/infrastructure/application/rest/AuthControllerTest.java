package com.revo.authservice.infrastructure.application.rest;

import com.revo.authservice.domain.dto.UserDto;
import com.revo.authservice.domain.exception.BadLoginException;
import com.revo.authservice.domain.exception.UsernameInUseException;
import com.revo.authservice.domain.port.UserService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = AuthController.class)
@RunWith(SpringRunner.class)
class AuthControllerTest {

    private static final String REGISTER_END_POINT = "/register";
    private static final String LOGIN_END_POINT = "/login";
    private static final String USERNAME_IN_USE = "USERNAMEINUSE";
    private static final String AUTHORIZE_END_POINT = "/authorize";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String EXAMPLE_TOKEN = "token";

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserService userService;

    private final UserDto userDto = new UserDto(null, "test", "test", "test@email.pl");

    @Test
    void shouldReturn200WhileLoggingUser() {
        //given
        //when
        when(userService.loginUser(any(UserDto.class))).thenReturn(Mono.just(userDto));
        //then
        webTestClient
                .post()
                .uri(LOGIN_END_POINT)
                .bodyValue(userDto)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldReturn401WhileLoggingUser() {
        //given
        //when
        when(userService.loginUser(any(UserDto.class))).thenReturn(Mono.error(new BadLoginException()));
        //then
        webTestClient
                .post()
                .uri(LOGIN_END_POINT)
                .bodyValue(userDto)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void shouldReturn201WhileRegisteringUser() {
        //given
        //when
        when(userService.createUser(any(UserDto.class))).thenReturn(Mono.just(userDto));
        //then
        webTestClient
                .post()
                .uri(REGISTER_END_POINT)
                .bodyValue(userDto)
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void shouldReturn400WhileRegisteringUser() {
        //given
        //when
        when(userService.createUser(any(UserDto.class))).thenReturn(Mono.error(new UsernameInUseException(USERNAME_IN_USE)));
        //then
        webTestClient
                .post()
                .uri(REGISTER_END_POINT)
                .bodyValue(userDto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldReturn200WhileTranslatingTokenOnUser() {
        //given
        //when
        when(userService.getUserFromToken(anyString())).thenReturn(Mono.just(userDto));
        //then
        webTestClient
                .post()
                .uri(AUTHORIZE_END_POINT)
                .header(AUTHORIZATION_HEADER, EXAMPLE_TOKEN)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldReturn401WhileTranslatingTokenOnUser() {
        //given
        //when
        when(userService.getUserFromToken(anyString())).thenReturn(Mono.error(new BadLoginException()));
        //then
        webTestClient
                .post()
                .uri(AUTHORIZE_END_POINT)
                .header(AUTHORIZATION_HEADER, EXAMPLE_TOKEN)
                .exchange()
                .expectStatus().isUnauthorized();
    }
}
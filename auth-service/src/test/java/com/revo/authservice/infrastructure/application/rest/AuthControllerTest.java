package com.revo.authservice.infrastructure.application.rest;

import com.revo.authservice.domain.dto.AuthorizedUser;
import com.revo.authservice.domain.User;
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
    private static final String TEST_NAME = "test";
    private static final String TEST_EMAIL = "test@email.pl";
    private static final String CONTENT_TYPE_HEADER_NAME = "Content-Type";
    private static final String CONTENT_TYPE_HEADER_VALUE = "application/json";

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserService userService;

    private final User user = new User(null, TEST_NAME, TEST_NAME, TEST_EMAIL);
    private final AuthorizedUser authorizedUser = new AuthorizedUser(TEST_NAME);

    @Test
    void shouldReturn200WhileLoggingUser() {
        //given
        //when
        when(userService.loginUser(any(User.class))).thenReturn(Mono.just(user));
        //then
        webTestClient
                .post()
                .uri(LOGIN_END_POINT)
                .bodyValue(user)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldReturn401WhileLoggingUser() {
        //given
        //when
        when(userService.loginUser(any(User.class))).thenReturn(Mono.error(new BadLoginException()));
        //then
        webTestClient
                .post()
                .uri(LOGIN_END_POINT)
                .bodyValue(user)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void shouldReturn201WhileRegisteringUser() {
        //given
        //when
        when(userService.createUser(any(User.class))).thenReturn(Mono.just(user));
        //then
        webTestClient
                .post()
                .uri(REGISTER_END_POINT)
                .bodyValue(user)
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void shouldReturn400WhileRegisteringUser() {
        //given
        //when
        when(userService.createUser(any(User.class))).thenReturn(Mono.error(new UsernameInUseException(USERNAME_IN_USE)));
        //then
        webTestClient
                .post()
                .uri(REGISTER_END_POINT)
                .bodyValue(user)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldReturn200WhileTranslatingTokenOnUser() {
        //given
        //when
        when(userService.getUsernameFromToken(anyString())).thenReturn(Mono.just(authorizedUser));
        //then
        webTestClient
                .post()
                .uri(AUTHORIZE_END_POINT)
                .header(AUTHORIZATION_HEADER, EXAMPLE_TOKEN)
                .header(CONTENT_TYPE_HEADER_NAME, CONTENT_TYPE_HEADER_VALUE)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldReturn401WhileTranslatingTokenOnUser() {
        //given
        //when
        when(userService.getUsernameFromToken(anyString())).thenReturn(Mono.error(new BadLoginException()));
        //then
        webTestClient
                .post()
                .uri(AUTHORIZE_END_POINT)
                .header(AUTHORIZATION_HEADER, EXAMPLE_TOKEN)
                .header(CONTENT_TYPE_HEADER_NAME, CONTENT_TYPE_HEADER_VALUE)
                .exchange()
                .expectStatus().isUnauthorized();
    }
}
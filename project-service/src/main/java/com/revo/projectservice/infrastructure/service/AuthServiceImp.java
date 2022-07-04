package com.revo.projectservice.infrastructure.service;

import com.revo.projectservice.domain.dto.AuthorizedUser;
import com.revo.projectservice.domain.exception.NoPermissionException;
import com.revo.projectservice.domain.port.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Service
class AuthServiceImp implements AuthService {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String GATEWAY_HOST = "http://localhost:8080";
    private static final String TRANSLATE_TOKEN_PATH = "/authorize";
    private static final String CONTENT_TYPE_HEADER_NAME = "Content-Type";
    private static final String CONTENT_TYPE_HEADER_VALUE = "application/json";

    @Override
    public Mono<AuthorizedUser> getAuthorizedUserMonoFromToken(String token) {
        return getUserFromAuthServiceAsResponse(token)
                .bodyToMono(AuthorizedUser.class);
    }

    private WebClient.ResponseSpec getUserFromAuthServiceAsResponse(String token) {
        return WebClient.create(GATEWAY_HOST)
                .post()
                .uri(TRANSLATE_TOKEN_PATH)
                .header(AUTHORIZATION_HEADER, token)
                .header(CONTENT_TYPE_HEADER_NAME, CONTENT_TYPE_HEADER_VALUE)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, throwNoPermissionException());
    }

    private Function<ClientResponse, Mono<? extends Throwable>> throwNoPermissionException() {
        return clientResponse -> Mono.error(new NoPermissionException());
    }

    @Override
    public Flux<AuthorizedUser> getAuthorizedUserFluxFromToken(String token) {
        return getUserFromAuthServiceAsResponse(token)
                .bodyToFlux(AuthorizedUser.class);
    }
}

package com.revo.projectservice.domain.port;

import com.revo.projectservice.domain.dto.AuthorizedUser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AuthService {
    Mono<AuthorizedUser> getAuthorizedUserMonoFromToken(String token);
    Flux<AuthorizedUser> getAuthorizedUserFluxFromToken(String token);
}

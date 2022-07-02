package com.revo.authservice.domain.port;

import com.revo.authservice.domain.dto.AuthorizedUser;
import com.revo.authservice.domain.User;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<User> createUser(User user);
    Mono<AuthorizedUser> getUsernameFromToken(String token);
    String getTokenFromUsername(String username);
    Mono<User> loginUser(User user);
}

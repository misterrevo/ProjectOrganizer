package com.revo.authservice.domain.port;

import com.revo.authservice.domain.User;
import reactor.core.publisher.Mono;

public interface UserRepository {
    Mono<User> saveUser(User user);
    Mono<Boolean> userExistsByEmail(String email);
    Mono<Boolean> userExistsByUsername(String username);
    Mono<User> getUserByUsername(String username);
}

package com.revo.authservice.domain.port;

import com.revo.authservice.domain.dto.UserDto;
import reactor.core.publisher.Mono;

public interface UserRepositoryPort {
    Mono<UserDto> save(Mono<UserDto> userDto);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    Mono<UserDto> getUserByUsername(String subject);
}

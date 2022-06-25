package com.revo.authservice.domain.port;

import com.revo.authservice.domain.dto.UserDto;
import reactor.core.publisher.Mono;

public interface UserRepositoryPort {
    Mono<UserDto> save(UserDto userDto);
    Mono<Boolean> existsByEmail(String email);
    Mono<Boolean> existsByUsername(String username);
    Mono<UserDto> getUserByUsername(String subject);
}

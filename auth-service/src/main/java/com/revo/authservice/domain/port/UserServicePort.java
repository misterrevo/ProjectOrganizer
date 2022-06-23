package com.revo.authservice.domain.port;

import com.revo.authservice.domain.dto.UserDto;
import reactor.core.publisher.Mono;

public interface UserServicePort {

    Mono<UserDto> createUser(Mono<UserDto> userDto);
    Mono<UserDto> getUserFromToken(Mono<String> token);
    Mono<String> getTokenFromUsername(Mono<String> username);
    Mono<UserDto> loginUser(Mono<UserDto> userDto);
}

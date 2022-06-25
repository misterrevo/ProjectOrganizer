package com.revo.authservice.domain.port;

import com.revo.authservice.domain.dto.UserDto;
import reactor.core.publisher.Mono;

public interface UserServicePort {

    Mono<UserDto> createUser(UserDto userDto);
    Mono<UserDto> getUserFromToken(String token);
    String getTokenFromUsername(String username);
    Mono<UserDto> loginUser(UserDto userDto);
}

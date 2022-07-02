package com.revo.authservice.domain.port;

import com.revo.authservice.domain.dto.AuthorizedUser;
import com.revo.authservice.domain.dto.UserDto;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<UserDto> createUser(UserDto userDto);
    Mono<AuthorizedUser> getUsernameFromToken(String token);
    String getTokenFromUsername(String username);
    Mono<UserDto> loginUser(UserDto userDto);
}

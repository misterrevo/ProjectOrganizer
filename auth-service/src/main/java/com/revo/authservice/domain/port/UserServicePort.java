package com.revo.authservice.domain.port;

import com.revo.authservice.domain.dto.UserDto;
import reactor.core.publisher.Mono;

public interface UserServicePort {

    UserDto createUser(UserDto userDto);
    UserDto  getUserFromToken(String token);
    String getTokenFromUsername(String username);
}

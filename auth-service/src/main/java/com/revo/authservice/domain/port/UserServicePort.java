package com.revo.authservice.domain.port;

import com.revo.authservice.domain.dto.UserDto;

public interface UserServicePort {

    UserDto createUser(UserDto userDto);
    UserDto getUserFromToken(String token);
    String getTokenFromUsername(String username);
    void loginUser(String login, String password);
}

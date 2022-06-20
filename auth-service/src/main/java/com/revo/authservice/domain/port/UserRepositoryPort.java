package com.revo.authservice.domain.port;

import com.revo.authservice.domain.dto.UserDto;

import java.util.Optional;

public interface UserRepositoryPort {
    UserDto save(UserDto userDto);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    Optional<UserDto> getUserByUsername(String subject);
}

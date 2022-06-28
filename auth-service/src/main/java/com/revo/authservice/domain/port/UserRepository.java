package com.revo.authservice.domain.port;

import com.revo.authservice.domain.dto.UserDto;
import reactor.core.publisher.Mono;

public interface UserRepository {
    Mono<UserDto> saveUser(UserDto userDto);
    Mono<Boolean> userExistsByEmail(String email);
    Mono<Boolean> userExistsByUsername(String username);
    Mono<UserDto> getUserByUsername(String username);
}

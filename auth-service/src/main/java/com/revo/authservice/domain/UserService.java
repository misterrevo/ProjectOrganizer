package com.revo.authservice.domain;

import com.revo.authservice.domain.dto.UserDto;
import com.revo.authservice.domain.exception.EmailInUseException;
import com.revo.authservice.domain.exception.UsernameInUseException;
import com.revo.authservice.domain.port.JwtPort;
import com.revo.authservice.domain.port.UserRepositoryPort;
import com.revo.authservice.domain.port.UserServicePort;
import reactor.core.publisher.Mono;

public class UserService implements UserServicePort {

    private static final String USER_TOPIC = "com.revo.users.topic";

    private final UserRepositoryPort userRepositoryPort;
    private final JwtPort jwtPort;

    public UserService(UserRepositoryPort userRepositoryPort, JwtPort jwtPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.jwtPort = jwtPort;
    }

    @Override
    public Mono<UserDto> createUser(UserDto userDto) {
        return checkExistsByEmail(userDto.email())
                .then(checkExistsByUsername(userDto.username()))
                .then(save(userDto));
    }

    private Mono<UserDto> save(UserDto userDto) {
        return userRepositoryPort.save(userDto);
    }

    private Mono<Boolean> checkExistsByUsername(String username) {
        return userRepositoryPort.existsByUsername(username)
                .flatMap(bool -> {
                    if(bool){
                        return Mono.error(new UsernameInUseException(username));
                    }
                    return Mono.just(bool);
                });
    }

    private Mono<Boolean> checkExistsByEmail(String email) {
        return userRepositoryPort.existsByEmail(email)
                .flatMap(bool -> {
                    if(bool){
                        return Mono.error(new EmailInUseException(email));
                    }
                    return Mono.just(bool);
                });
    }

    @Override
    public Mono<UserDto> getUserFromToken(String token) {
        return null;
    }

    @Override
    public Mono<String> getTokenFromUsername(String username) {
        return null;
    }

    @Override
    public Mono<UserDto> loginUser(UserDto userDto) {
        return null;
    }
}

package com.revo.authservice.domain;

import com.revo.authservice.domain.dto.UserDto;
import com.revo.authservice.domain.exception.BadLoginException;
import com.revo.authservice.domain.exception.EmailInUseException;
import com.revo.authservice.domain.exception.UserNotFoundException;
import com.revo.authservice.domain.exception.UsernameInUseException;
import com.revo.authservice.domain.port.JwtPort;
import com.revo.authservice.domain.port.UserRepositoryPort;
import com.revo.authservice.domain.port.UserServicePort;
import com.revo.authservice.infrastructure.application.rest.dto.LoginDto;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static com.revo.authservice.domain.Mapper.fromDto;
import static com.revo.authservice.domain.Mapper.toDto;

public class UserService implements UserServicePort {

    private static final String USER_TOPIC = "com.revo.users.topic";

    private final UserRepositoryPort userRepositoryPort;
    private final JwtPort jwtPort;

    public UserService(UserRepositoryPort userRepositoryPort, JwtPort jwtPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.jwtPort = jwtPort;
    }

    @Override
    public Mono<UserDto> createUser(Mono<UserDto> userDto) {
        userDto.flatMap(dto -> {
            String email = dto.email();
            if(existsInBaseByEmail(email)){
                throw new EmailInUseException(email);
            }
            String username = dto.username();
            if(existsInBaseByUsername(username)){
                throw new UsernameInUseException(username);
            }
            return Mono.just(dto);
        }).thenMany(userRepositoryPort.save(userDto));
        return userDto;
    }

    private boolean existsInBaseByUsername(String username) {
        return userRepositoryPort.existsByUsername(username);
    }

    private boolean existsInBaseByEmail(String email) {
        return userRepositoryPort.existsByEmail(email);
    }

    @Override
    public Mono<UserDto> getUserFromToken(Mono<String> token) {
        return null;
    }

    @Override
    public Mono<String> getTokenFromUsername(Mono<String> username) {
        return null;
    }

    @Override
    public Mono<UserDto> loginUser(Mono<UserDto> userDto) {
        return null;
    }
}

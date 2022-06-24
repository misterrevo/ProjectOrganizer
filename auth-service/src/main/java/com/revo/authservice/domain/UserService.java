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
        userDto
                .thenMany(existsInBaseByEmail(userDto.map(dto -> dto.email())))
                .thenMany(existsInBaseByUsername(userDto.map(dto -> dto.username())))
                .thenMany(userRepositoryPort.save(userDto))
                .subscribe();
        return userDto;
    }

    private Mono<Boolean> existsInBaseByUsername(Mono<String> username) {
        return userRepositoryPort.existsByUsername(username)
                .flatMap(bool -> {
                    if(bool){
//                        throw new UsernameInUseException(username.block());
                        return Mono.error(new UsernameInUseException(username.block()));
                    }
                    return Mono.just(bool);
                });
    }

    private Mono<Boolean> existsInBaseByEmail(Mono<String> email) {
        return userRepositoryPort.existsByEmail(email)
                .flatMap(bool -> {
            if(bool){
//                throw new EmailInUseException(email.block());
                return Mono.error(new EmailInUseException(email.block()));
            }
            return Mono.just(bool);
        });
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

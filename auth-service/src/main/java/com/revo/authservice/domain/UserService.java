package com.revo.authservice.domain;

import com.revo.authservice.domain.dto.UserDto;
import com.revo.authservice.domain.exception.BadLoginException;
import com.revo.authservice.domain.exception.EmailInUseException;
import com.revo.authservice.domain.exception.UsernameInUseException;
import com.revo.authservice.domain.port.JwtPort;
import com.revo.authservice.domain.port.UserRepositoryPort;
import com.revo.authservice.domain.port.UserServicePort;
import reactor.core.publisher.Mono;

import java.util.Objects;

public class UserService implements UserServicePort {
    
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
        return userRepositoryPort
                .getUserByUsername(getSubject(token));
    }

    private String getSubject(String token) {
        return jwtPort.getSubject(token);
    }

    @Override
    public Mono<String> getTokenFromUsername(String username) {
        return Mono
                .just(createToken(username));
    }

    private String createToken(String username) {
        return jwtPort.createToken(username);
    }

    @Override
    public Mono<UserDto> loginUser(UserDto userDto) {
        return userRepositoryPort.getUserByUsername(userDto.username())
                .flatMap(dto -> checkCredentials(dto, userDto));
    }

    private Mono<UserDto> checkCredentials(UserDto baseDto, UserDto requestDto) {
        if(passwordNotMatch(baseDto.password(), requestDto.password())){
            return Mono.error(new BadLoginException());
        }
        return Mono.just(baseDto);
    }

    private boolean passwordNotMatch(String basePassword, String requestPassword) {
        return !Objects.equals(basePassword, requestPassword);
    }

}

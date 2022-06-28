package com.revo.authservice.domain;

import com.revo.authservice.domain.dto.UserDto;
import com.revo.authservice.domain.exception.BadLoginException;
import com.revo.authservice.domain.exception.EmailInUseException;
import com.revo.authservice.domain.exception.UsernameInUseException;
import com.revo.authservice.domain.port.EncoderPort;
import com.revo.authservice.domain.port.JwtPort;
import com.revo.authservice.domain.port.UserRepositoryPort;
import com.revo.authservice.domain.port.UserServicePort;
import reactor.core.publisher.Mono;

public class UserService implements UserServicePort {

    private final UserRepositoryPort userRepositoryPort;
    private final JwtPort jwtPort;
    private final EncoderPort encoderPort;

    public UserService(UserRepositoryPort userRepositoryPort, JwtPort jwtPort, EncoderPort encoderPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.jwtPort = jwtPort;
        this.encoderPort = encoderPort;
    }

    @Override
    public Mono<UserDto> createUser(UserDto userDto) {
        return checkUserExistsByEmail(userDto.email())
                .then(checkUserExistsByUsername(userDto.username()))
                .then(encodePassword(userDto))
                .then(saveUser(userDto));
    }

    private Mono<UserDto> encodePassword(UserDto userDto) {
        return Mono.just(userDto)
                .map(dto -> new UserDto(userDto.id(), userDto.username(), encoderPort.encodePassword(userDto.password()), userDto.email()));
    }

    private Mono<UserDto> saveUser(UserDto userDto) {
        return userRepositoryPort.saveUser(userDto);
    }

    private Mono<Boolean> checkUserExistsByUsername(String username) {
        return userRepositoryPort.userExistsByUsername(username)
                .flatMap(bool -> {
                    if(bool){
                        return Mono.error(new UsernameInUseException(username));
                    }
                    return Mono.just(bool);
                });
    }

    private Mono<Boolean> checkUserExistsByEmail(String email) {
        return userRepositoryPort.userExistsByEmail(email)
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
                .getUserByUsername(getSubjectFromToken(token));
    }

    private String getSubjectFromToken(String token) {
        return jwtPort.getSubjectFromToken(token);
    }

    @Override
    public String getTokenFromUsername(String username) {
        return createToken(username);
    }

    private String createToken(String username) {
        return jwtPort.createTokenFromUsername(username);
    }

    @Override
    public Mono<UserDto> loginUser(UserDto userDto) {
        return userRepositoryPort.getUserByUsername(userDto.username())
                .flatMap(dto -> checkPasswordMatches(dto, userDto))
                .switchIfEmpty(Mono.error(new BadLoginException()));
    }

    private Mono<UserDto> checkPasswordMatches(UserDto baseDto, UserDto requestDto) {
        if(passwordNotMatch(baseDto.password(), requestDto.password())){
            return Mono.error(new BadLoginException());
        }
        return Mono.just(baseDto);
    }

    private boolean passwordNotMatch(String basePassword, String requestPassword) {
        return !encoderPort.passwordMatches(requestPassword, basePassword);
    }

}

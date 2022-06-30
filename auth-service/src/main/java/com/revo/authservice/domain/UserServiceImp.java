package com.revo.authservice.domain;

import com.revo.authservice.domain.dto.UserDto;
import com.revo.authservice.domain.exception.BadLoginException;
import com.revo.authservice.domain.exception.EmailInUseException;
import com.revo.authservice.domain.exception.UsernameInUseException;
import com.revo.authservice.domain.port.Encoder;
import com.revo.authservice.domain.port.Jwt;
import com.revo.authservice.domain.port.UserRepository;
import com.revo.authservice.domain.port.UserService;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final Jwt jwt;
    private final Encoder encoder;

    public UserServiceImp(UserRepository userRepository, Jwt jwt, Encoder encoder) {
        this.userRepository = userRepository;
        this.jwt = jwt;
        this.encoder = encoder;
    }

    @Override
    public Mono<UserDto> createUser(UserDto userDto) {
        return checkUserExistsByEmail(userDto.email())
                .then(checkUserExistsByUsername(userDto.username()))
                .then(encodePassword(userDto))
                .flatMap(targetUserDto -> saveUser(targetUserDto));
    }

    private Mono<UserDto> encodePassword(UserDto userDto) {
        return Mono.just(userDto)
                .map(encodePasswordInDto(userDto));
    }

    private Function<UserDto, UserDto> encodePasswordInDto(UserDto userDto) {
        return targetUserDto -> new UserDto(userDto.id(), userDto.username(), encoder.encodePassword(userDto.password()), userDto.email());
    }

    private Mono<UserDto> saveUser(UserDto userDto) {
        return userRepository.saveUser(userDto);
    }

    private Mono<Boolean> checkUserExistsByUsername(String username) {
        return existsByUsernameMono(username)
                .flatMap(existsByUsernameBoolean -> getMonoBooleanOrError(existsByUsernameBoolean, getUsernameInUseError(username)));
    }

    private Mono<Boolean> getMonoBooleanOrError(Boolean existsByUsernameBoolean, Mono<Boolean> username) {
        if(existsByUsernameBoolean){
            return username;
        }
        return Mono.just(existsByUsernameBoolean);
    }

    private Mono<Boolean> existsByUsernameMono(String username) {
        return userRepository.userExistsByUsername(username);
    }

    private Mono<Boolean> getUsernameInUseError(String username) {
        return Mono.error(new UsernameInUseException(username));
    }

    private Mono<Boolean> checkUserExistsByEmail(String email) {
        return existsByEmailMono(email)
                .flatMap(existsByEmailBoolean -> getMonoBooleanOrError(existsByEmailBoolean, getEmailInUseError(email)));
    }

    private Mono<Boolean> getEmailInUseError(String email) {
        return Mono.error(new EmailInUseException(email));
    }

    private Mono<Boolean> existsByEmailMono(String email) {
        return userRepository.userExistsByEmail(email);
    }

    @Override
    public Mono<UserDto> getUserFromToken(String token) {
        return userRepository
                .getUserByUsername(getSubjectFromToken(token));
    }

    private String getSubjectFromToken(String token) {
        return jwt.getSubjectFromToken(token);
    }

    @Override
    public String getTokenFromUsername(String username) {
        return createToken(username);
    }

    private String createToken(String username) {
        return jwt.createTokenFromUsername(username);
    }

    @Override
    public Mono<UserDto> loginUser(UserDto userDto) {
        return userRepository.getUserByUsername(userDto.username())
                .flatMap(targetUserDto -> checkPasswordMatches(targetUserDto, userDto))
                .switchIfEmpty(getBadLoginError());
    }

    private Mono<UserDto> getBadLoginError() {
        return Mono.error(new BadLoginException());
    }

    private Mono<UserDto> checkPasswordMatches(UserDto baseDto, UserDto requestDto) {
        if(passwordNotMatch(baseDto.password(), requestDto.password())){
            return getBadLoginError();
        }
        return Mono.just(baseDto);
    }

    private boolean passwordNotMatch(String basePassword, String requestPassword) {
        return !encoder.passwordMatches(requestPassword, basePassword);
    }

}

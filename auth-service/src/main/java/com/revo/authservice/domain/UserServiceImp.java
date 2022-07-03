package com.revo.authservice.domain;

import com.revo.authservice.domain.dto.AuthorizedUser;
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
    public Mono<User> createUser(User user) {
        return checkUserExistsByEmail(user.email())
                .then(checkUserExistsByUsername(user.username()))
                .then(encodePassword(user))
                .flatMap(this::saveUser);
    }

    private Mono<User> encodePassword(User user) {
        return Mono.just(user)
                .map(encodePasswordInDto(user));
    }

    private Function<User, User> encodePasswordInDto(User user) {
        return targetUserDto -> new User(user.id(), user.username(), encoder.encodePassword(user.password()), user.email());
    }

    private Mono<User> saveUser(User user) {
        return userRepository.saveUser(user);
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
    public Mono<AuthorizedUser> getUsernameFromToken(String token) {
        return userRepository
                .getUserByUsername(getSubjectFromToken(token))
                .map(userDto -> new AuthorizedUser(userDto.username()));
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
    public Mono<User> loginUser(User user) {
        return userRepository.getUserByUsername(user.username())
                .filter(targetUserDto -> passwordMatch(targetUserDto.password(), user.password()))
                .switchIfEmpty(getBadLoginError());
    }

    private Mono<User> getBadLoginError() {
        return Mono.error(new BadLoginException());
    }

    private boolean passwordMatch(String basePassword, String requestPassword) {
        return encoder.passwordMatches(requestPassword, basePassword);
    }
}

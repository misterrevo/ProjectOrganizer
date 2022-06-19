package com.revo.authservice.domain;

import com.revo.authservice.domain.dto.UserDto;
import com.revo.authservice.domain.exception.BadLoginException;
import com.revo.authservice.domain.exception.EmailInUseException;
import com.revo.authservice.domain.exception.UserNotFoundException;
import com.revo.authservice.domain.exception.UsernameInUseException;
import com.revo.authservice.domain.port.JwtPort;
import com.revo.authservice.domain.port.UserRepositoryPort;
import com.revo.authservice.domain.port.UserServicePort;

import java.util.Objects;

import static com.revo.authservice.domain.Mapper.fromDto;
import static com.revo.authservice.domain.Mapper.toDto;

public class UserService implements UserServicePort {

    private final UserRepositoryPort userRepositoryPort;
    private final JwtPort jwtPort;

    public UserService(UserRepositoryPort userRepositoryPort, JwtPort jwtPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.jwtPort = jwtPort;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = fromDto(userDto);
        if(existsByEmail(user.getEmail())){
            throw new EmailInUseException(user.getEmail());
        }
        if(existsByUsername(user.getUsername())){
            throw new UsernameInUseException(user.getUsername());
        }
        return save(toDto(user));
    }

    private boolean existsByUsername(String username) {
        return userRepositoryPort.existsByUsername(username);
    }

    private boolean existsByEmail(String email) {
        return userRepositoryPort.existsByEmail(email);
    }

    private UserDto save(UserDto userDto) {
        return userRepositoryPort.save(userDto);
    }

    @Override
    public UserDto getUserFromToken(String token) {
        String subject = getSubject(token);
        return getUser(subject);
    }

    private UserDto getUser(String subject) {
        return userRepositoryPort.getUserByUsername(subject)
                .orElseThrow(() ->  new UserNotFoundException(subject));
    }

    private String getSubject(String token) {
        return jwtPort.getSubject(token);
    }

    @Override
    public String getTokenFromUsername(String username) {
        return createToken(username);
    }

    @Override
    public UserDto loginUser(String login, String password) {
        UserDto userDto = getUserByUsername(login);
        User user = fromDto(userDto);
        if(passwordNotMatch(user.getPassword(), password)){
            throw new BadLoginException();
        }
        return toDto(user);
    }

    private boolean passwordNotMatch(String password, String password1) {
        return Objects.equals(password, password1);
    }

    private UserDto getUserByUsername(String login) {
        return userRepositoryPort.getUserByUsername(login)
                .orElseThrow(() -> new BadLoginException());
    }

    private String createToken(String username) {
        return jwtPort.createToken(username);
    }
}

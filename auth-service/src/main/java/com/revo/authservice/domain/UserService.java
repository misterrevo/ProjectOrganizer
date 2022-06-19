package com.revo.authservice.domain;

import com.revo.authservice.domain.dto.UserDto;
import com.revo.authservice.domain.exception.EmailInUseException;
import com.revo.authservice.domain.exception.UserNotFoundException;
import com.revo.authservice.domain.exception.UsernameInUseException;
import com.revo.authservice.domain.port.JwtPort;
import com.revo.authservice.domain.port.UserRepositoryPort;
import com.revo.authservice.domain.port.UserServicePort;

class UserService implements UserServicePort {

    private final UserRepositoryPort userRepositoryPort;
    private final JwtPort jwtPort;

    UserService(UserRepositoryPort userRepositoryPort, JwtPort jwtPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.jwtPort = jwtPort;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        if(existsByEmail(userDto.email())){
            throw new EmailInUseException(userDto.email());
        }
        if(existsByUsername(userDto.username())){
            throw new UsernameInUseException(userDto.username());
        }
        return save(userDto);
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

    private String createToken(String username) {
        return jwtPort.createToken(username);
    }
}

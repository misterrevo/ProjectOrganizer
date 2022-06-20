package com.revo.authservice.domain;

import com.revo.authservice.domain.dto.UserDto;
import com.revo.authservice.domain.exception.BadLoginException;
import com.revo.authservice.domain.exception.EmailInUseException;
import com.revo.authservice.domain.exception.UserNotFoundException;
import com.revo.authservice.domain.exception.UsernameInUseException;
import com.revo.authservice.domain.port.BrokerPort;
import com.revo.authservice.domain.port.JwtPort;
import com.revo.authservice.domain.port.UserRepositoryPort;
import com.revo.authservice.domain.port.UserServicePort;

import java.util.Objects;

import static com.revo.authservice.domain.Mapper.fromDto;
import static com.revo.authservice.domain.Mapper.toDto;

public class UserService implements UserServicePort {

    private static final String USER_TOPIC = "com.revo.users.topic";

    private final UserRepositoryPort userRepositoryPort;
    private final JwtPort jwtPort;
    private final BrokerPort brokerPort;

    public UserService(UserRepositoryPort userRepositoryPort, JwtPort jwtPort, BrokerPort brokerPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.jwtPort = jwtPort;
        this.brokerPort = brokerPort;
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
        UserDto savedUser = save(toDto(user));
        brokerPort.send(USER_TOPIC, savedUser);
        return savedUser;
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
        try {
            String subject = getSubject(token);
            return getUser(subject);
        } catch (Exception exception){
            throw new BadLoginException();
        }
    }

    private UserDto getUser(String subject) {
        return userRepositoryPort.getUserByUsername(subject)
                .orElseThrow(() -> new UserNotFoundException(subject));
    }

    private String getSubject(String token) {
        return jwtPort.getSubject(token);
    }

    @Override
    public String getTokenFromUsername(String username) {
        return createToken(username);
    }

    @Override
    public void loginUser(String login, String password) {
        try{
            UserDto userDto = getUser(login);
            User user = fromDto(userDto);
            if(passwordNotMatch(user.getPassword(), password)){
                throw new BadLoginException();
            }
        }catch (UserNotFoundException exception){
            throw new BadLoginException();
        }
    }

    private boolean passwordNotMatch(String password, String password1) {
        return Objects.equals(password, password1);
    }


    private String createToken(String username) {
        return jwtPort.createToken(username);
    }
}

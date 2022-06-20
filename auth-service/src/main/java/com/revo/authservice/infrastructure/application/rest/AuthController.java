package com.revo.authservice.infrastructure.application.rest;

import com.revo.authservice.domain.dto.UserDto;
import com.revo.authservice.domain.port.UserServicePort;
import com.revo.authservice.infrastructure.application.rest.dto.LoginDto;
import com.revo.authservice.infrastructure.application.rest.dto.RegisterDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.net.URI;

import static com.revo.authservice.infrastructure.application.rest.DtoMapper.Mapper;

@RestController
class AuthController {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String AUTHORIZATION_HEADER_PREFIX = "Bearer ";
    private static final String USERS_LOCATION = "None";
    private final UserServicePort userServicePort;

    AuthController(UserServicePort userServicePort) {
        this.userServicePort = userServicePort;
    }

    @PostMapping("/login")
    ResponseEntity<UserDto> loginUser(@RequestBody LoginDto loginDto){
        userServicePort.loginUser(loginDto.getUsername(), loginDto.getPassword());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(AUTHORIZATION_HEADER, AUTHORIZATION_HEADER_PREFIX + userServicePort.getTokenFromUsername(loginDto.getUsername()));
        return ResponseEntity.ok().headers(httpHeaders).build();
    }

    @PostMapping("/register")
    ResponseEntity<UserDto> registerUser(@RequestBody RegisterDto registerDto){
        return ResponseEntity.created(URI.create(USERS_LOCATION)).body(userServicePort.createUser(Mapper.fromRegister(registerDto)));
    }

    @PostMapping("/authorize")
    ResponseEntity<UserDto> translateTokenOnUser(@RequestHeader(AUTHORIZATION_HEADER) String token){
        return ResponseEntity.ok(userServicePort.getUserFromToken(token));
    }
}

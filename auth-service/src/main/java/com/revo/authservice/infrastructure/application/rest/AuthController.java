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
    Mono<ResponseEntity<UserDto>> loginUser(@RequestBody Mono<LoginDto> loginDto){
        return null;
    }

    @PostMapping("/register")
    Mono<ResponseEntity<UserDto>> registerUser(@RequestBody Mono<RegisterDto> registerDto){
        return registerDto
                .map(Mapper::fromRegister)
                .flatMap(target -> userServicePort.createUser(Mono.just(target)))
                .map(dto -> ResponseEntity.created(URI.create(USERS_LOCATION)).body(dto));
//        return userServicePort.createUser(registerDto.map(Mapper::fromRegister))
//                .map(dto -> ResponseEntity.created(URI.create(USERS_LOCATION)).body(dto));
    }

    @PostMapping("/authorize")
    Mono<ResponseEntity<UserDto>> translateTokenOnUser(@RequestHeader(AUTHORIZATION_HEADER) String token){
        return null;
    }
}

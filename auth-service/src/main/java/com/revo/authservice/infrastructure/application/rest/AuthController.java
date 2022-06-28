package com.revo.authservice.infrastructure.application.rest;

import com.revo.authservice.domain.dto.UserDto;
import com.revo.authservice.domain.port.UserService;
import com.revo.authservice.infrastructure.application.rest.dto.LoginDto;
import com.revo.authservice.infrastructure.application.rest.dto.RegisterDto;
import org.springframework.http.ResponseEntity;
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
    private final UserService userService;

    AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    Mono<ResponseEntity<UserDto>> loginUser(@RequestBody Mono<LoginDto> loginDto){
        return loginDto
                .map(Mapper::fromLogin)
                .flatMap(userDto -> userService.loginUser(userDto))
                .map(userDto -> ResponseEntity.ok().header(AUTHORIZATION_HEADER, userService.getTokenFromUsername(userDto.username())).body(userDto));
    }

    @PostMapping("/register")
    Mono<ResponseEntity<UserDto>> registerUser(@RequestBody Mono<RegisterDto> registerDto){
        return registerDto
                .map(Mapper::fromRegister)
                .flatMap(userDto -> userService.createUser(userDto))
                .map(userDto -> ResponseEntity.created(URI.create(USERS_LOCATION)).body(userDto));
    }

    @PostMapping("/authorize")
    Mono<ResponseEntity<UserDto>> translateTokenOnUser(@RequestHeader(AUTHORIZATION_HEADER) String token){
        return Mono.just(token)
                .flatMap(targetToken -> userService.getUserFromToken(targetToken))
                .map(userDto -> ResponseEntity.ok(userDto));
    }
}

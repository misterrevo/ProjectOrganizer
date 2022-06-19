package com.revo.authservice.infrastructure.application.rest;

import com.revo.authservice.domain.dto.UserDto;
import com.revo.authservice.domain.port.UserServicePort;
import com.revo.authservice.infrastructure.application.rest.dto.LoginDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class AuthController {

    private final UserServicePort userServicePort;

    AuthController(UserServicePort userServicePort) {
        this.userServicePort = userServicePort;
    }

    @PostMapping("/login")
    ResponseEntity<UserDto> login(@RequestBody LoginDto loginDto){
        return ResponseEntity.ok(userServicePort.loginUser(loginDto.getUsername(), loginDto.getPassword()));
    }
}

package com.revo.authservice.infrastructure.application.config;

import com.revo.authservice.domain.UserService;
import com.revo.authservice.domain.port.JwtPort;
import com.revo.authservice.domain.port.UserRepositoryPort;
import com.revo.authservice.domain.port.UserServicePort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(value = "com.revo.authservice.infrastructure")
class BeanConfiguration {

    @Bean
    public UserServicePort userServicePort(UserRepositoryPort userRepositoryPort, JwtPort jwtPort){
        return new UserService(userRepositoryPort, jwtPort);
    }
}

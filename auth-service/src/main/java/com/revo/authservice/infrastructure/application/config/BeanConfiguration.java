package com.revo.authservice.infrastructure.application.config;

import com.revo.authservice.domain.UserService;
import com.revo.authservice.domain.port.JwtPort;
import com.revo.authservice.domain.port.UserRepositoryPort;
import com.revo.authservice.domain.port.UserServicePort;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@ComponentScan(value = "com.revo.authservice.infrastructure")
@EnableMongoRepositories(basePackages = "com.revo.authservice.infrastructure")
@PropertySource(value = "classpath:jwt.yml", factory = YamlFactory.class)
class BeanConfiguration {

    @Bean
    public UserServicePort userServicePort(UserRepositoryPort userRepositoryPort, JwtPort jwtPort){
        return new UserService(userRepositoryPort, jwtPort);
    }
}

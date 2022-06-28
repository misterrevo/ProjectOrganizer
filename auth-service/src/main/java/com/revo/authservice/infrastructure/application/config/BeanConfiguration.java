package com.revo.authservice.infrastructure.application.config;

import com.revo.authservice.domain.UserServiceImp;
import com.revo.authservice.domain.port.Encoder;
import com.revo.authservice.domain.port.Jwt;
import com.revo.authservice.domain.port.UserRepository;
import com.revo.authservice.domain.port.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@ComponentScan(value = "com.revo.authservice.infrastructure")
@EnableReactiveMongoRepositories(basePackages = "com.revo.authservice.infrastructure")
@PropertySource(value = "classpath:jwt.yml", factory = YamlFactory.class)
class BeanConfiguration {

    @Bean
    public UserService createUserServiceBean(UserRepository userRepository, Jwt jwt, Encoder encoder){
        return new UserServiceImp(userRepository, jwt, encoder);
    }
}

package com.revo.projectservice.infrastructure.application.config;

import com.revo.projectservice.domain.Service;
import com.revo.projectservice.domain.port.ProjectRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@ComponentScan(value = "com.revo.projectservice.infrastructure")
@EnableReactiveMongoRepositories(basePackages = "com.revo.projectservice.infrastructure")
class BeanConfiguration {

    @Bean
    public Service createServiceBean(ProjectRepositoryPort projectRepositoryPort){
        return new Service(projectRepositoryPort);
    }

}

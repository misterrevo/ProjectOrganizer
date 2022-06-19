package com.revo.authservice.infrastructure.database;

import com.revo.authservice.domain.dto.UserDto;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Repository
interface UserRepository extends ReactiveMongoRepository<UserEntity, String> {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    Mono<UserEntity> findByUsername(String subject);
}

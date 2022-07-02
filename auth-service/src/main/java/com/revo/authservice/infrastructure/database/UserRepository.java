package com.revo.authservice.infrastructure.database;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
interface UserRepository extends ReactiveMongoRepository<UserEntity, String> {
    Mono<Boolean> existsByEmail(String email);
    Mono<Boolean> existsByUsername(String username);
    Mono<UserEntity> findByUsername(String username);
}

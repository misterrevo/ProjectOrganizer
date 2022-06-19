package com.revo.authservice.infrastructure.database;

import com.revo.authservice.domain.dto.UserDto;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import java.util.Optional;

interface UserRepository extends ReactiveMongoRepository<UserEntity, String> {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    Optional<UserDto> findByUsername(String subject);
}

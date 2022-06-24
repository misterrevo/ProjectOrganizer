package com.revo.authservice.infrastructure.database;

import com.revo.authservice.domain.dto.UserDto;
import com.revo.authservice.domain.port.UserRepositoryPort;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static com.revo.authservice.infrastructure.database.UserMapper.Mapper;

@Component
class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserRepository userRepository;

    UserRepositoryAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Mono<UserDto> save(Mono<UserDto> userDto) {
        Mono<UserEntity> userEntity = userDto
                .map(Mapper::fromDto);
        Mono<UserEntity> savedUser = insert(userEntity);
        return savedUser
                .map(Mapper::toDto);
    }

    private Mono<UserEntity> insert(Mono<UserEntity> userEntity) {
        return userEntity
                .flatMap(
                        entity -> userRepository.save(entity)
                );
    }

    @Override
    public Mono<Boolean> existsByEmail(Mono<String> email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Mono<Boolean> existsByUsername(Mono<String> username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public Mono<UserDto> getUserByUsername(String username) {
        return getFromBase(username)
                .map(Mapper::toDto);
    }

    private Mono<UserEntity> getFromBase(String username) {
        return userRepository.findByUsername(username);
    }
}

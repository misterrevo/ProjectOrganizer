package com.revo.authservice.infrastructure.database;

import com.revo.authservice.domain.dto.UserDto;
import com.revo.authservice.domain.port.UserRepositoryPort;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static com.revo.authservice.infrastructure.database.UserMapper.Mapper;

@Component
class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserRepository userRepository;

    UserRepositoryAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto save(UserDto userDto) {
        UserEntity userEntity = Mapper.fromDto(userDto);
        Mono<UserEntity> savedUser = userRepository.insert(userEntity);
        return Mapper.toDto(savedUser.block());
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public Optional<UserDto> getUserByUsername(String username) {
        return Optional.of(getDomain(username));
    }

    private UserDto getDomain(String username) {
        return Mapper.toDto(getFromBase(username));
    }

    private UserEntity getFromBase(String username) {
        return userRepository.findByUsername(username).block();
    }
}

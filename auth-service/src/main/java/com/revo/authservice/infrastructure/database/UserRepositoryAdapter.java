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
    public Mono<UserDto> save(UserDto userDto) {
        return userRepository.save(Mapper.fromDto(userDto))
                .map(Mapper::toDto);
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Mono<Boolean> existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public Mono<UserDto> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(Mapper::toDto);
    }
}

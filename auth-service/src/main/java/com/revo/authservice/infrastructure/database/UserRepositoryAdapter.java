package com.revo.authservice.infrastructure.database;

import com.revo.authservice.domain.dto.UserDto;
import com.revo.authservice.domain.port.UserRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static com.revo.authservice.infrastructure.database.UserMapper.Mapper;

@Component
class UserRepositoryAdapter implements UserRepository {

    private final com.revo.authservice.infrastructure.database.UserRepository userRepository;

    UserRepositoryAdapter(com.revo.authservice.infrastructure.database.UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Mono<UserDto> saveUser(UserDto userDto) {
        return userRepository.save(Mapper.mapUserEntityFromDto(userDto))
                .map(Mapper::mapUserEntityToDto);
    }

    @Override
    public Mono<Boolean> userExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Mono<Boolean> userExistsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public Mono<UserDto> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(Mapper::mapUserEntityToDto);
    }
}

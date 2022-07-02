package com.revo.authservice.infrastructure.database;

import com.revo.authservice.domain.User;
import com.revo.authservice.domain.port.UserRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static com.revo.authservice.infrastructure.database.UserMapper.Mapper;

@Component
class UserRepositoryImp implements UserRepository {
    private final com.revo.authservice.infrastructure.database.UserRepository userRepository;

    UserRepositoryImp(com.revo.authservice.infrastructure.database.UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Mono<User> saveUser(User user) {
        return getSavedUserEntity(user)
                .map(Mapper::mapUserEntityToDto);
    }

    private Mono<UserEntity> getSavedUserEntity(User user) {
        return userRepository.save(Mapper.mapUserEntityFromDto(user));
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
    public Mono<User> getUserByUsername(String username) {
        return getUserEntityByUsername(username)
                .map(Mapper::mapUserEntityToDto);
    }

    private Mono<UserEntity> getUserEntityByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}

package com.revo.authservice.infrastructure.database;

import com.revo.authservice.domain.dto.UserDto;

class Mapper {

    static UserEntity toEntity(UserDto userDto){
        return UserEntity.Builder.anUserEntity()
                .id(userDto.id())
                .email(userDto.email())
                .password(userDto.password())
                .username(userDto.username())
                .build();
    }

    static UserDto toDomain(UserEntity userEntity){
        return new UserDto(userEntity.getId(), userEntity.getUsername(), userEntity.getPassword(), userEntity.getEmail());
    }
}

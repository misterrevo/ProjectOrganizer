package com.revo.authservice.infrastructure.database;

import com.revo.authservice.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
interface UserMapper {
    UserMapper Mapper = Mappers.getMapper(UserMapper.class);

    User mapUserEntityToDto(UserEntity entity);
    UserEntity mapUserEntityFromDto(User user);
}

package com.revo.authservice.infrastructure.database;

import com.revo.authservice.domain.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
interface UserMapper {

    UserMapper Mapper = Mappers.getMapper(UserMapper.class);

    UserDto mapUserEntityToDto(UserEntity entity);
    UserEntity mapUserEntityFromDto(UserDto userDto);
}

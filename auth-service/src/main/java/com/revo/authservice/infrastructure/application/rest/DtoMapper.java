package com.revo.authservice.infrastructure.application.rest;

import com.revo.authservice.domain.dto.UserDto;
import com.revo.authservice.infrastructure.application.rest.dto.LoginDto;
import com.revo.authservice.infrastructure.application.rest.dto.RegisterDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
interface DtoMapper {
    DtoMapper Mapper = Mappers.getMapper(DtoMapper.class);

    UserDto fromRegister(RegisterDto registerDto);
    UserDto fromLogin(LoginDto loginDto);
}

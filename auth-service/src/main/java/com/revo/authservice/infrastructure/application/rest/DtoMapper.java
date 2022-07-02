package com.revo.authservice.infrastructure.application.rest;

import com.revo.authservice.domain.User;
import com.revo.authservice.infrastructure.application.rest.dto.LoginDto;
import com.revo.authservice.infrastructure.application.rest.dto.RegisterDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
interface DtoMapper {
    DtoMapper Mapper = Mappers.getMapper(DtoMapper.class);

    User fromRegister(RegisterDto registerDto);
    User fromLogin(LoginDto loginDto);
}

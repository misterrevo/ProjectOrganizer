package com.revo.authservice.domain;

import com.revo.authservice.domain.dto.UserDto;
import reactor.core.publisher.Mono;

class Mapper {

    static User fromDto(UserDto userDto){
        return User.Builder.anUser()
                .username(userDto.username())
                .password(userDto.password())
                .email(userDto.email())
                .build();
    }

    static UserDto toDto(User user){
        return new UserDto(user.getUsername(), user.getPassword(), user.getEmail());
    }
}
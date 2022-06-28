package com.revo.authservice.domain;

import com.revo.authservice.domain.dto.UserDto;

class Mapper {

    static User mapUserFromDto(UserDto userDto){
        return User.Builder.anUser()
                .id(userDto.id())
                .username(userDto.username())
                .password(userDto.password())
                .email(userDto.email())
                .build();
    }

    static UserDto mapUserToDto(User user){
        return new UserDto(user.getId(), user.getUsername(), user.getPassword(), user.getEmail());
    }
}

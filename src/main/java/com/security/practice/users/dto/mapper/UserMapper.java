package com.security.practice.users.dto.mapper;

import com.security.practice.users.dto.request.UserRequest;
import com.security.practice.users.dto.response.UserResponse;
import com.security.practice.users.model.User;

public class UserMapper {

    public static User toEntity(UserRequest userRequest){
        return User.builder()
                .username(userRequest.getUsername())
                .password(userRequest.getPassword())
                .build();

    }

    public static UserResponse toDto(User user){
        return UserResponse.builder()
                .username(user.getUsername())
                .build();
    }

}

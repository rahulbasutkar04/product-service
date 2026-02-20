package com.project.store.domain.user.response;

import com.project.store.domain.user.User;
import com.project.store.domain.user.UserRole;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder(toBuilder = true)
public class UserResponse {

    private String contact;

    private String email;

    private String name;

    private UserRole role;


    public static UserResponse buildUserResponseFromUserEntity(User user) {

        return UserResponse.builder()
                .email(user.getEmail())
                .contact(user.getContact())
                .name(user.getName())
                .role(user.getRole())
                .build();
    }

}

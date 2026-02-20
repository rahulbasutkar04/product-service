package com.project.store.domain.user.auth.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class AuthResponse {

    private String email;

    private String refreshToken;

    private String token;

}

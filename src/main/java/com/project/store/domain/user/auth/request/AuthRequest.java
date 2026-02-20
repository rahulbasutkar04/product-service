package com.project.store.domain.user.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class AuthRequest {

    @Size(min = 6, message = "Password length should be at least 6")
    private String password;

    @Email(message = "Enter valid email address")
    @NotBlank(message = "Email should not be empty")
    private String email;
}

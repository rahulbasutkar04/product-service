package com.project.store.domain.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Request POJO {@link com.project.store.domain.user.User}
 */
@Getter
@Setter
@Data
public class UserRequest {

    @NotBlank(message = "Contact number should not be empty")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Enter valid 10 digit mobile number")
    private String contact;

    @Email(message = "Enter valid email address")
    @NotBlank(message = "Email should not be empty")
    private String email;

    @NotBlank(message = "Name should not be empty")
    private String name;

    @Size(min = 6, message = "Password length should be at least 6")
    private String password;

}
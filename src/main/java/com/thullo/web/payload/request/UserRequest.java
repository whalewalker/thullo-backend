package com.thullo.web.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class UserRequest {
    @NotBlank(message = "first name can not be blank")
    private String name;

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email can not be blank")
    private String email;

    @Size(min = 6, max = 20, message = "Invalid password")
    @NotBlank(message = "Password can not be blank")
    private String password;
}

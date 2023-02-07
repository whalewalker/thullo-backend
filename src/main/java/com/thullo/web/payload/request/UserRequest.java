package com.thullo.web.payload.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


@Data
public class UserRequest {
    @NotBlank(message = "first name can not be blank")
    private String name;

    @Email(regexp = ".+[@].+[\\.].+", message = "Invalid email")
    @NotBlank(message = "Email can not be blank")
    private String email;

    @Size(min = 6, max = 20, message = "Invalid password, password must be between 6 to 20 characters")
    @NotBlank(message = "Password can not be blank")
    private String password;
}

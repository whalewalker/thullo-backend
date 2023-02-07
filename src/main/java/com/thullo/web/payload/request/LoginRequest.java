package com.thullo.web.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @Email(regexp = ".+[@].+[\\.].+", message = "Invalid email")
    @NotBlank(message="email cannot be blank")
    private String email;

    @Size(min = 6, max = 20, message = "Invalid password, password must be between 6 to 20 characters")
    @NotBlank(message="password cannot be blank")
    private String password;
}

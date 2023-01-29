package com.thullo.web.payload.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordRequest {
    private String token;

    @Size(min = 6, max = 20, message = "Password cannot be blank")
    private String password;

    private String oldPassword;
}

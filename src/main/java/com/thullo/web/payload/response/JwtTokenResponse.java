package com.thullo.web.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class JwtTokenResponse {
    private String jwtToken;

    private String refreshToken;

    private String email;
}

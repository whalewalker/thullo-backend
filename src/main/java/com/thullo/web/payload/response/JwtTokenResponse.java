package com.thullo.web.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JwtTokenResponse {
    private String jwtToken;

    private String refreshToken;

    private String email;
}

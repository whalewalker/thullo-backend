package com.thullo.web.payload.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TokenResponse {
       private String token;
       private  String tokenType;
       private LocalDateTime expiryDate;
}

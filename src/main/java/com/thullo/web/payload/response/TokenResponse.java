package com.thullo.web.payload.response;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class TokenResponse extends RepresentationModel<TokenResponse> {
       private String token;
       private  String tokenType;
       private LocalDateTime expiryDate;

}

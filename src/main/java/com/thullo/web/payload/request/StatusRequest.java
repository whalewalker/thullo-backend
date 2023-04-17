package com.thullo.web.payload.request;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class StatusRequest {
    @NotBlank(message = "This field cannot be blank")
    private String previousStatus;
    @NotBlank(message = "This field cannot be blank")
    private String currentStatus;
    private String requestUrl;
}
package com.thullo.web.payload.request;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.Set;

@Getter
@Setter
public class StatusRequest {
    @NotBlank(message = "This field cannot be blank")
    private String status;
    private String requestUrl;

    @NotBlank(message = "This field cannot be blank")
    private Set<String> boardRef;
}

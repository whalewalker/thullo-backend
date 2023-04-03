package com.thullo.web.payload.request;


import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class StatusRequest {
    private String status;
    private String requestUrl;
    private Set<String> boardRef;
}

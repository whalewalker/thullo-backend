package com.thullo.web.payload.request;


import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class StatusRequest {
    private String previousStatus;
    private String currentStatus;
    private String requestUrl;
    private Set<String> boardRef;
}

package com.thullo.web.payload.request;

import lombok.Data;

@Data
public class UserProfileRequest {
    private String name;
    private String email;
    private String bio;

    private String phoneNumber;

//    private String password;

    private String imageUrl;
}

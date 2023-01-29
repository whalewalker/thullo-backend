package com.thullo.web.payload.response;

import lombok.Data;

@Data
public class UserProfileResponse {
    private String name;
    private String email;
    private String bio;

    private String phoneNumber;

    private String password;

    private String imageUrl;
}

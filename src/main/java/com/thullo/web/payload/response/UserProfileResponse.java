package com.thullo.web.payload.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserProfileResponse {

    private Long id;
    private String name;

    private String email;

    private String phoneNumber;

    private String imageUrl;

    private String bio;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

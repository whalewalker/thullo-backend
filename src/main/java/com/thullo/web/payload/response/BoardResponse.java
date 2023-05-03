package com.thullo.web.payload.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BoardResponse {
    private Long id;
    private String name;
    private String imageUrl;
    private String boardVisibility;
    private String description;
    private String boardTag;
    private UserResponse createdBy;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
}

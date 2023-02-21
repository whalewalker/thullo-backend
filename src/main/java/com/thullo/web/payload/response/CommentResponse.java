package com.thullo.web.payload.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentResponse {
    private String message;
    private String createdBy;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

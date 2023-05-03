package com.thullo.web.payload.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
public class BoardResponse {
    private Long id;
    private String name;
    private String imageUrl;
    private String boardVisibility;
    private String description;
    private String boardTag;
    private List<TaskColumnResponse> taskColumn;
    private Set<UserResponse> collaborators;
    private UserResponse createdBy;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
}

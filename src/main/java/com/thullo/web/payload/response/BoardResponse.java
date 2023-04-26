package com.thullo.web.payload.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Data
public class BoardResponse {
    private Long id;
    private String name;
    private String imageUrl;
    private String boardVisibility;
    private String boardTag;
    private List<TaskColumnResponse> taskColumn = new ArrayList<>();
    private Set<UserResponse> collaborators = new LinkedHashSet<>();
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
}

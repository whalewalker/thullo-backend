package com.thullo.web.payload.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class TaskColumnResponse {
    private Long id;
    private String name;
    private List<TaskResponse> tasks = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

package com.thullo.web.payload.response;

import com.thullo.data.model.Task;
import com.thullo.data.model.User;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class BoardResponse {
    private Long id;
    private String name;
    private String imageUrl;
    private String boardVisibility;
    private String boardTag;
    private List<Column> taskColumn = new ArrayList<>();
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
    private Set<User> collaborators = new HashSet<>();

    @Data
    public static class Column {
        private String name;
        private List<Task> tasks = new ArrayList<>();
    }
}

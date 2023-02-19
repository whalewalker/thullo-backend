package com.thullo.web.payload.response;

import com.thullo.data.model.Task;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class BoardResponse {
    private Long id;

    private String name;

    private String imageUrl;

    private String boardTag;

    private List<Column> taskColumn = new ArrayList<>();

    private LocalDateTime updatedAt;

    private LocalDateTime createdAt;


    @Data
    public static class Column {
        private String name;
        private List<Task> tasks = new ArrayList<>();
    }
}

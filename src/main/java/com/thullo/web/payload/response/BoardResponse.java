package com.thullo.web.payload.response;

import com.thullo.data.model.TaskColumn;
import lombok.Data;

import java.util.List;

@Data
public class BoardResponse {
    private String name;
    private String imageUrl;
    private List<TaskColumn> taskColumns;
}

package com.thullo.web.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskMoveRequest {
    private Long taskId;
    private Long newColumnId;
    private Long index;
}

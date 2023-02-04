package com.thullo.web.controller;

import com.thullo.annotation.CurrentTaskColumn;
import com.thullo.service.TaskService;
import com.thullo.web.payload.request.TaskRequest;
import com.thullo.web.payload.request.TaskResponse;
import com.thullo.web.payload.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/thullo")
public class TaskController {
    private final TaskService taskService;

    @PostMapping(value = "/create-task/{taskColumnId}",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @CurrentTaskColumn
    public ResponseEntity<ApiResponse> createTask(@PathVariable Long taskColumnId, TaskRequest taskRequest, HttpServletRequest request) {
        taskRequest.setRequestUrl(request.getRequestURL().toString());
        taskRequest.setTaskColumnId(taskColumnId);
        TaskResponse taskResponse = taskService.createTask(taskRequest);
        return ResponseEntity.ok(new ApiResponse(true, "Task created successfully", taskResponse));
    }


}

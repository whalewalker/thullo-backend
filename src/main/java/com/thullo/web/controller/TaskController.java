package com.thullo.web.controller;

import com.thullo.annotation.CurrentTaskColumn;
import com.thullo.data.model.Task;
import com.thullo.service.TaskService;
import com.thullo.web.exception.BadRequestException;
import com.thullo.web.exception.RecordNotFoundException;
import com.thullo.web.payload.request.TaskMoveRequest;
import com.thullo.web.payload.request.TaskRequest;
import com.thullo.web.payload.request.TaskResponse;
import com.thullo.web.payload.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/thullo")
public class TaskController {
    private final TaskService taskService;

    @PostMapping(value = "/create-task/{taskColumnId}",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("@taskServiceImpl.isTaskOwner(#taskColumnId, authentication.principal.email)")
    @CurrentTaskColumn
    public ResponseEntity<ApiResponse> createTask(@PathVariable Long taskColumnId, TaskRequest taskRequest, HttpServletRequest request) {
        taskRequest.setRequestUrl(request.getRequestURL().toString());
        taskRequest.setTaskColumnId(taskColumnId);
        try {
            TaskResponse taskResponse = taskService.createTask(taskRequest);
            return ResponseEntity.ok(new ApiResponse(true, "Task created successfully", taskResponse));
        } catch (BadRequestException | IOException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
        }
    }

    @PutMapping("/move")
    @PreAuthorize("@taskServiceImpl.isTaskOwnedByUser(#request.taskId, #request.newColumnId, authentication.principal.email)")
    public ResponseEntity<?> moveTask(@RequestBody TaskMoveRequest request) {
        try {
            Task task = taskService.moveTask(request.getTaskId(), request.getNewColumnId(), request.getIndex());
            return new ResponseEntity<>(task, HttpStatus.OK);
        } catch (RecordNotFoundException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
        }
    }
}

package com.thullo.web.controller;

import com.thullo.annotation.CurrentTaskColumn;
import com.thullo.data.model.Task;
import com.thullo.service.TaskService;
import com.thullo.web.exception.BadRequestException;
import com.thullo.web.exception.ResourceNotFoundException;
import com.thullo.web.payload.request.TaskMoveRequest;
import com.thullo.web.payload.request.TaskRequest;
import com.thullo.web.payload.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/thullo/tasks")
public class TaskController {
    private final TaskService taskService;

    @PostMapping(value = "/{taskColumnId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("@taskServiceImpl.isTaskOwner(#taskColumnId, authentication.principal.email)")
    @CurrentTaskColumn
    public ResponseEntity<ApiResponse> createTask(@PathVariable Long taskColumnId, TaskRequest taskRequest, HttpServletRequest request) {
        taskRequest.setRequestUrl(request.getRequestURL().toString());
        taskRequest.setTaskColumnId(taskColumnId);
        try {
            Task task = taskService.createTask(taskRequest);
            return ResponseEntity.ok(new ApiResponse(true, "Task created successfully", task));
        } catch (BadRequestException | IOException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
        }
    }

    @PutMapping("/move")
    @PreAuthorize("@taskServiceImpl.isTaskOwnedByUser(#request.taskId, #request.newColumnId, authentication.principal.email)")
    public ResponseEntity<ApiResponse> moveTask(@RequestBody TaskMoveRequest request) {
        try {
            Task task = taskService.moveTask(request.getTaskId(), request.getNewColumnId(), request.getPosition());
            return ResponseEntity.ok(new ApiResponse(true, "Task moved successfully", task));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
        }
    }

    @GetMapping("/{taskId}")
    @PreAuthorize("@taskServiceImpl.isTaskCreator(#taskId, authentication.principal.email)")
    public ResponseEntity<ApiResponse> getTask(@PathVariable("taskId") Long taskId) {
        try {
            Task task = taskService.getTask(taskId);
            return ResponseEntity.ok(new ApiResponse(true, "Task fetched successfully", task));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
        }
    }


    @PutMapping(value = "/{taskId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("@taskServiceImpl.isTaskCreator(#taskId, authentication.principal.email)")
    public ResponseEntity<ApiResponse> editTask(@PathVariable Long taskId, TaskRequest taskRequest, HttpServletRequest request) {
        taskRequest.setRequestUrl(request.getRequestURL().toString());
        try {
            Task task = taskService.editTask(taskId, taskRequest);
            return ResponseEntity.ok(new ApiResponse(true, "Task created successfully", task));
        } catch (ResourceNotFoundException | BadRequestException | IOException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
        }
    }

    @DeleteMapping("/{taskId}")
    @PreAuthorize("@taskServiceImpl.isTaskCreator(#taskId, authentication.principal.email)")
    public ResponseEntity<ApiResponse> deleteATask(@PathVariable("taskId") Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.ok(new ApiResponse(true, "Task delete successfully"));
    }


    @GetMapping("/{taskId}/contributors")
    public ResponseEntity<ApiResponse> getContributors(@PathVariable(value = "taskId") Long taskId) {
        try {
            Task task = taskService.getTask(taskId);
            return ResponseEntity.ok(new ApiResponse(true, "fetch contributors successfully", task.getContributors()));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
        }
    }

    @GetMapping
    public List<Task> searchTasks(@RequestParam("search") String search, @RequestParam("boardRef") String boardRef) {
        return taskService.findTaskContainingNameOrBoardId(search, boardRef);
    }

    @PutMapping("/{boardRef}")
    public ResponseEntity<ApiResponse> addContributors(@PathVariable String boardRef, @RequestBody Set<String> contributors) {
        try {
            taskService.addContributors(boardRef, contributors);
            return ResponseEntity.ok(new ApiResponse(true, "contributors successfully added"));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
        }
    }


    @PutMapping("remove/{boardRef}")
    public ResponseEntity<ApiResponse> removeContributors(@PathVariable String boardRef, @RequestBody Set<String> contributors) {
        try {
            taskService.removeContributors(boardRef, contributors);
            return ResponseEntity.ok(new ApiResponse(true, "contributors successfully removed"));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
        }
    }

    @PutMapping("/add-cover-image")
    public ResponseEntity<ApiResponse> addCoverImage(@RequestParam("boardRef") String boardRef, @RequestParam("file") MultipartFile file, HttpServletRequest request) {
        try {
            Task task = taskService.updateTaskImage(boardRef, file, request.getRequestURL().toString());
            return ResponseEntity.ok(new ApiResponse(true, "cover image added successfully", task));
        } catch (BadRequestException | ResourceNotFoundException | IOException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
        }
    }
}

package com.thullo.web.controller;

import com.thullo.annotation.CurrentUser;
import com.thullo.data.model.Task;
import com.thullo.security.UserPrincipal;
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
import java.util.Map;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/thullo/tasks")
public class TaskController {
    private final TaskService taskService;

    @PostMapping(value = "/{boardTag}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("#boardServiceImpl.hasBoardRole(#principal.email, #boardTag) or hasRole('BOARD_' + #boardTag)")
    public ResponseEntity<ApiResponse> createTask(@PathVariable String boardTag, TaskRequest taskRequest, HttpServletRequest request, @CurrentUser UserPrincipal principal) {
        taskRequest.setRequestUrl(request.getRequestURL().toString());
        try {
            Task task = taskService.createTask(boardTag, taskRequest);
            return ResponseEntity.ok(new ApiResponse(true, "Task created successfully", task));
        } catch (BadRequestException | ResourceNotFoundException | IOException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
        }
    }

    @PutMapping("{boardTag}/move")
    @PreAuthorize("#boardServiceImpl.hasBoardRole(#principal.email, #boardTag) or hasRole('BOARD_' + #boardTag) or hasRole('TASK_' + #request.boardRef)")
    public ResponseEntity<ApiResponse> moveTask(@PathVariable String boardTag, @RequestBody TaskMoveRequest request, @CurrentUser UserPrincipal principal) {
        try {
            Task task = taskService.moveTask(request.getBoardRef(), request.getStatus(), request.getPosition());
            return ResponseEntity.ok(new ApiResponse(true, "Task moved successfully", task));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
        }
    }

    @GetMapping("{boardTag}/{boardRef}")
    @PreAuthorize("#boardServiceImpl.hasBoardRole(#principal.email, #boardTag) or hasRole('BOARD_' + #boardTag) or hasRole('TASK_' + #boardRef)")
    public ResponseEntity<ApiResponse> getTask(@PathVariable String boardTag, @PathVariable String boardRef, @CurrentUser UserPrincipal principal) {
        try {
            Task task = taskService.getTask(boardRef);
            return ResponseEntity.ok(new ApiResponse(true, "Task fetched successfully", task));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
        }
    }


    @PutMapping(value = "{boardTag}/{boardRef}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("#boardServiceImpl.hasBoardRole(#principal.email, #boardTag) or hasRole('BOARD_' + #boardTag) or hasRole('TASK_' + #boardRef)")
    public ResponseEntity<ApiResponse> editTask(@PathVariable String boardTag, @PathVariable String boardRef, TaskRequest taskRequest, HttpServletRequest request, @CurrentUser UserPrincipal principal) {
        taskRequest.setRequestUrl(request.getRequestURL().toString());
        try {
            Task task = taskService.editTask(boardRef, taskRequest);
            return ResponseEntity.ok(new ApiResponse(true, "Task created successfully", task));
        } catch (ResourceNotFoundException | BadRequestException | IOException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
        }
    }

    @DeleteMapping("{boardTag}/{boardRef}")
    @PreAuthorize("#boardServiceImpl.hasBoardRole(#principal.email, #boardTag) or hasRole('BOARD_' + #boardTag)")
    public ResponseEntity<ApiResponse> deleteATask(@PathVariable String boardTag, @PathVariable String boardRef, @CurrentUser UserPrincipal principal) {
        try {
            taskService.deleteTask(boardRef);
            return ResponseEntity.ok(new ApiResponse(true, "Task delete successfully"));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
        }
    }


    @GetMapping("{boardTag}/{boardRef}/contributors")
    @PreAuthorize("#boardServiceImpl.hasBoardRole(#principal.email, #boardTag) or hasRole('BOARD_' + #boardTag) or hasRole('TASK_' + #boardRef)")
    public ResponseEntity<ApiResponse> getContributors(@PathVariable String boardTag, @PathVariable String boardRef, @CurrentUser UserPrincipal principal) {
        try {
            Task task = taskService.getTask(boardRef);
            return ResponseEntity.ok(new ApiResponse(true, "fetch contributors successfully", task.getContributors()));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
        }
    }

    @GetMapping("/search")
    public List<Task> searchTasks(@RequestParam("params") String search, @RequestParam("boardRef") String boardRef) {
        return taskService.findTaskContainingNameOrBoardId(search, boardRef);
    }

    @PutMapping("{boardTag}/{boardRef}/contributors")
    @PreAuthorize("#boardServiceImpl.hasBoardRole(#principal.email, #boardTag) or hasRole('BOARD_' + #boardTag) or hasRole('BOARD_' + #boardTag)")
    public ResponseEntity<ApiResponse> addContributors(@PathVariable String boardTag, @PathVariable String boardRef, @RequestBody Set<String> contributors, @CurrentUser UserPrincipal principal) {
        try {
            taskService.addContributors(boardRef, contributors);
            return ResponseEntity.ok(new ApiResponse(true, "contributors successfully added"));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
        }
    }


    @PutMapping("{boardTag}/{boardRef}/remove/contributors")
    @PreAuthorize("#boardServiceImpl.hasBoardRole(#principal.email, #boardTag) or hasRole('BOARD_' + #boardTag)")
    public ResponseEntity<ApiResponse> removeContributors(@PathVariable String boardTag, @PathVariable String boardRef, @RequestBody Set<String> contributors, @CurrentUser UserPrincipal principal) {
        try {
            taskService.removeContributors(boardRef, contributors);
            return ResponseEntity.ok(new ApiResponse(true, "contributors successfully removed"));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
        }
    }

    @PutMapping("{boardTag}/{boardRef}/cover-image")
    @PreAuthorize("#boardServiceImpl.hasBoardRole(#principal.email, #boardTag) or hasRole('BOARD_' + #boardTag) or hasRole('TASK_' + #boardRef)")
    public ResponseEntity<ApiResponse> addCoverImage(@PathVariable String boardTag, @PathVariable String boardRef, @RequestParam("file") MultipartFile file, HttpServletRequest request, @CurrentUser UserPrincipal principal) {
        try {
            Task task = taskService.updateTaskImage(boardRef, file, request.getRequestURL().toString());
            return ResponseEntity.ok(new ApiResponse(true, "cover image added successfully", task));
        } catch (BadRequestException | ResourceNotFoundException | IOException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
        }
    }


    @GetMapping("{boardTag}/{boardRef}/cover-image")
    @PreAuthorize("#boardServiceImpl.hasBoardRole(#principal.email, #boardTag) or hasRole('BOARD_' + #boardTag) or hasRole('TASK_' + #boardRef)")
    public ResponseEntity<ApiResponse> getTaskCoverImage(@PathVariable String boardTag, @PathVariable String boardRef, @CurrentUser UserPrincipal principal) {
        try {
            String taskImageUrl = taskService.getTaskImageUrl(boardRef);
            return ResponseEntity.ok(new ApiResponse(true, "cover image fetched successfully", Map.of("imageUrl", taskImageUrl)));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
        }
    }

}

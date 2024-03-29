package com.thullo.web.controller;

import com.thullo.annotation.CurrentUser;
import com.thullo.data.model.Attachment;
import com.thullo.security.UserPrincipal;
import com.thullo.service.TaskService;
import com.thullo.web.exception.BadRequestException;
import com.thullo.web.exception.ResourceNotFoundException;
import com.thullo.web.exception.UserException;
import com.thullo.web.payload.request.TaskMoveRequest;
import com.thullo.web.payload.request.TaskRequest;
import com.thullo.web.payload.response.ApiResponse;
import com.thullo.web.payload.response.TaskResponse;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/thullo/tasks")
public class TaskController {
    private final TaskService taskService;

    @PostMapping(value = "/{boardTag}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("@boardServiceImpl.hasBoardRole(authentication.principal.email, #boardTag) or hasRole('BOARD_' + #boardTag)")
    public ResponseEntity<ApiResponse> createTask(@PathVariable String boardTag, TaskRequest taskRequest, HttpServletRequest request, @CurrentUser UserPrincipal principal) {
        taskRequest.setRequestUrl(request.getRequestURL().toString());
        try {
            TaskResponse task = taskService.createTask(boardTag, principal.getEmail(), taskRequest);
            return ResponseEntity.ok(new ApiResponse(true, "Task created successfully", task));
        } catch (BadRequestException | ResourceNotFoundException | IOException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
        }
    }

    @PutMapping("{boardTag}/move")
    @PreAuthorize("@boardServiceImpl.hasBoardRole(authentication.principal.email, #boardTag) or hasRole('BOARD_' + #boardTag) or hasRole('TASK_' + #request.boardRef)")
    public ResponseEntity<ApiResponse> moveTask(@PathVariable String boardTag, @RequestBody TaskMoveRequest request) {
        try {
            TaskResponse task = taskService.moveTask(request.getBoardRef(), request.getTaskColumnId(), request.getPosition());
            return ResponseEntity.ok(new ApiResponse(true, "Task moved successfully", task));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
        }
    }

    @GetMapping("{boardTag}/{boardRef}")
    @PreAuthorize("@boardServiceImpl.hasBoardRole(authentication.principal.email, #boardTag) or hasRole('BOARD_' + #boardTag) or hasRole('TASK_' + #boardRef)")
    public ResponseEntity<ApiResponse> getTask(@PathVariable String boardTag, @PathVariable String boardRef) {
        try {
            TaskResponse task = taskService.getTask(boardRef);
            return ResponseEntity.ok(new ApiResponse(true, "Task fetched successfully", task));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
        }
    }


    @PutMapping(value = "{boardTag}/{boardRef}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("@boardServiceImpl.hasBoardRole(authentication.principal.email, #boardTag) or hasRole('BOARD_' + #boardTag) or hasRole('TASK_' + #boardRef)")
    public ResponseEntity<ApiResponse> editTask(@PathVariable String boardTag, @PathVariable String boardRef, TaskRequest taskRequest, HttpServletRequest request) {
        taskRequest.setRequestUrl(request.getRequestURL().toString());
        try {
            TaskResponse task = taskService.editTask(boardRef, taskRequest);
            return ResponseEntity.ok(new ApiResponse(true, "Task created successfully", task));
        } catch (ResourceNotFoundException | BadRequestException | IOException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
        }
    }

    @DeleteMapping("{boardTag}/{boardRef}")
    @PreAuthorize("@boardServiceImpl.hasBoardRole(authentication.principal.email, #boardTag) or hasRole('BOARD_' + #boardTag)")
    public ResponseEntity<ApiResponse> deleteATask(@PathVariable String boardTag, @PathVariable String boardRef) {
        try {
            taskService.deleteTask(boardRef);
            return ResponseEntity.ok(new ApiResponse(true, "Task delete successfully"));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
        }
    }


    @GetMapping("{boardTag}/{boardRef}/contributors")
    @PreAuthorize("@boardServiceImpl.hasBoardRole(authentication.principal.email, #boardTag) or hasRole('BOARD_' + #boardTag) or hasRole('TASK_' + #boardRef)")
    public ResponseEntity<ApiResponse> getContributors(@PathVariable String boardTag, @PathVariable String boardRef) {
        try {
            TaskResponse task = taskService.getTask(boardRef);
            return ResponseEntity.ok(new ApiResponse(true, "fetch contributors successfully", task.getContributors()));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
        }
    }

    @GetMapping("/search")
    public List<TaskResponse> searchTasks(@RequestParam("params") String searchParams) {
        return taskService.searchTask(searchParams);
    }

    @PutMapping("{boardTag}/{boardRef}/contributor/{contributorEmail}")
    @PreAuthorize("@boardServiceImpl.hasBoardRole(authentication.principal.email, #boardTag) or hasRole('BOARD_' + #boardTag) or hasRole('BOARD_' + #boardTag) and #contributorEmail != #authentication.principal.emai")
    public ResponseEntity<ApiResponse> addContributor(@PathVariable String boardTag, @PathVariable String boardRef, @PathVariable String contributorEmail) {
        try {
            taskService.addContributor(boardRef, contributorEmail);
            return ResponseEntity.ok(new ApiResponse(true, "contributors successfully added"));
        } catch (ResourceNotFoundException | UserException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
        }
    }


    @PutMapping("{boardTag}/{boardRef}/remove/contributor/{contributorEmail}")
    @PreAuthorize("@boardServiceImpl.hasBoardRole(authentication.principal.email, #boardTag) or hasRole('BOARD_' + #boardTag) and #contributorEmail != #authentication.principal.email")
    public ResponseEntity<ApiResponse> removeContributor(@PathVariable String boardTag, @PathVariable String boardRef, @PathVariable String contributorEmail) {
        try {
            taskService.removeContributor(boardRef, contributorEmail);
            return ResponseEntity.ok(new ApiResponse(true, "contributors successfully removed"));
        } catch (ResourceNotFoundException | UserException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
        }
    }


    @GetMapping("{boardTag}/{boardRef}/cover-image")
    @PreAuthorize("@boardServiceImpl.hasBoardRole(authentication.principal.email, #boardTag) or hasRole('BOARD_' + #boardTag) or hasRole('TASK_' + #boardRef)")
    public ResponseEntity<ApiResponse> getTaskCoverImage(@PathVariable String boardTag, @PathVariable String boardRef) {
        try {
            String taskImageUrl = taskService.getTaskImageUrl(boardRef);
            return ResponseEntity.ok(new ApiResponse(true, "cover image fetched successfully", Map.of("imageUrl", taskImageUrl)));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
        }
    }

    @PostMapping("{boardTag}/{boardRef}/add-attachment")
    @PreAuthorize("@boardServiceImpl.hasBoardRole(authentication.principal.email, #boardTag) or hasRole('BOARD_' + #boardTag) or hasRole('TASK_' + #boardRef)")
    public ResponseEntity<ApiResponse> addAttachment(@PathVariable String boardTag, @PathVariable String boardRef, @RequestParam("file") MultipartFile file, HttpServletRequest request) {
        try {
            Attachment attachment = taskService.addAttachmentToTask(boardRef, request.getRequestURL().toString(), file);
            return ResponseEntity.ok(new ApiResponse(true, "Attachment added successfully", attachment));
        } catch (BadRequestException | ResourceNotFoundException | IOException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
        }
    }

    @DeleteMapping("{boardTag}/{boardRef}/{attachmentId}")
    @PreAuthorize("@boardServiceImpl.hasBoardRole(authentication.principal.email, #boardTag) or hasRole('BOARD_' + #boardTag) or hasRole('TASK_' + #boardRef)")
    public ResponseEntity<ApiResponse> addAttachment(@PathVariable String boardTag, @PathVariable String boardRef, @PathVariable Long attachmentId, HttpServletRequest request) {
        taskService.deleteAttachmentFromTask(request.getRequestURL().toString(), attachmentId);
        return ResponseEntity.ok(new ApiResponse(true, "Attachment is  successfully deleted"));
    }

}
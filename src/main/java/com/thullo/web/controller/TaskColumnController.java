package com.thullo.web.controller;

import com.thullo.annotation.CurrentUser;
import com.thullo.security.UserPrincipal;
import com.thullo.service.TaskColumnService;
import com.thullo.web.exception.BadRequestException;
import com.thullo.web.exception.ResourceNotFoundException;
import com.thullo.web.exception.ThulloException;
import com.thullo.web.exception.UserException;
import com.thullo.web.payload.request.TaskColumnRequest;
import com.thullo.web.payload.response.ApiResponse;
import com.thullo.web.payload.response.TaskColumnResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/thullo/task-columns")
public class TaskColumnController {
    private final TaskColumnService taskColumnService;

    @PreAuthorize("@boardServiceImpl.hasBoardRole(authentication.principal.email, #boardTag) or hasRole('BOARD_' + #boardTag)")
    @PostMapping("/{boardTag}")
    public ResponseEntity<ApiResponse> createTaskColumn(@PathVariable String boardTag, @RequestBody @Valid TaskColumnRequest taskColumnRequest, @CurrentUser UserPrincipal principal) {
        try {
            TaskColumnResponse taskColumn = taskColumnService.createTaskColumn(taskColumnRequest, boardTag, principal);
            return ResponseEntity.ok(new ApiResponse(true, "Task column created successfully", taskColumn));
        } catch (ThulloException | BadRequestException | UserException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
        }
    }

    @PreAuthorize("@boardServiceImpl.hasBoardRole(authentication.principal.email, #boardTag) or hasRole('BOARD_' + #boardTag)")
    @PutMapping("/{boardTag}")
    public ResponseEntity<ApiResponse> updateTaskColumn(@PathVariable String boardTag, @RequestBody @Valid TaskColumnRequest taskColumnRequest, @CurrentUser UserPrincipal principal) {
        try {
            TaskColumnResponse taskColumn = taskColumnService.editTaskColumn(taskColumnRequest, principal);
            return ResponseEntity.ok(new ApiResponse(true, "Task column is edited successfully", taskColumn));
        } catch (ResourceNotFoundException | ThulloException | UserException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
        }
    }

    @PreAuthorize("@boardServiceImpl.hasBoardRole(authentication.principal.email, #boardTag) or hasRole('BOARD_' + #boardTag)")
    @DeleteMapping("/{boardTag}")
    public ResponseEntity<ApiResponse> deleteTaskColumn(@PathVariable String boardTag, @RequestBody @Valid TaskColumnRequest taskColumnRequest, @CurrentUser UserPrincipal principal) {
        try {
            taskColumnService.deleteTaskColumn(taskColumnRequest, boardTag, principal);
            return ResponseEntity.ok(new ApiResponse(true, "Task column deleted successfully"));
        } catch (UserException | BadRequestException | ResourceNotFoundException | ThulloException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
        }
    }
}

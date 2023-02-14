package com.thullo.web.controller;

import com.thullo.data.model.Label;
import com.thullo.service.LabelService;
import com.thullo.web.exception.ResourceNotFoundException;
import com.thullo.web.exception.ThulloException;
import com.thullo.web.payload.request.LabelRequest;
import com.thullo.web.payload.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/thullo/labels")
public class LabelController {

    private final LabelService labelService;


    @PostMapping
    public ResponseEntity<ApiResponse> createLabel(@RequestParam("boardRef") String boardRef, @RequestBody @Valid LabelRequest request) {
        try {
            Label label = labelService.createLabel(boardRef, request);
            return ResponseEntity.ok(new ApiResponse(true, "Label successfully created", label));
        } catch (ThulloException | ResourceNotFoundException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
        }
    }

    @PutMapping("/remove")
    public ResponseEntity<ApiResponse> removeLabelFromTask(@RequestParam("labelId") Long labelId, @RequestParam("boardRef") String boardRef) {
        try {
            labelService.removeLabelFromTask(labelId, boardRef);
            return ResponseEntity.ok(new ApiResponse(true, "Label is  successfully removed from task"));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
        }
    }

    @PutMapping
    public ResponseEntity<ApiResponse> updateLabel(@RequestParam("labelId") Long labelId, @RequestParam("boardRef") String boardRef, @RequestBody LabelRequest request) {
        try {
            Label label = labelService.updateLabelOnTask(boardRef, labelId, request);
            return ResponseEntity.ok(new ApiResponse(true, "Label updated successfully", label));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
        }
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<ApiResponse> getBoardLabel(@PathVariable Long boardId) {
        try {
            List<Label> labels = labelService.getBoardLabel(boardId);
            return ResponseEntity.ok(new ApiResponse(true, "Labels successfully fetched", labels));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
        }
    }
}

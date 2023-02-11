package com.thullo.web.controller;

import com.thullo.service.NotificationService;
import com.thullo.web.exception.ResourceNotFoundException;
import com.thullo.web.payload.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("api/v1/thullo/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> markNotificationAsViewed(@PathVariable("id") Long id) {
        try {
            notificationService.markNotificationAsViewed(id);
            return ResponseEntity.ok(new ApiResponse(true, "Notification marked as viewed"));
        } catch (
                ResourceNotFoundException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
        }
    }
}

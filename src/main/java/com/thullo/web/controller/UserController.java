package com.thullo.web.controller;


import com.thullo.security.CurrentUser;
import com.thullo.security.UserPrincipal;
import com.thullo.service.UserService;
import com.thullo.web.payload.request.UserProfileRequest;
import com.thullo.web.payload.response.ApiResponse;
import com.thullo.web.payload.response.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping("api/v1/thullo/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse> getUserDetails(@CurrentUser UserPrincipal userPrincipal) {
        UserProfileResponse userDetails = userService.getUserDetails(userPrincipal.getEmail());
        return ResponseEntity.ok(new ApiResponse(
                true, "User data successfully retrieved", userDetails));
    }

    @PostMapping("/edit")
    public ResponseEntity<ApiResponse> updateUserDetails(@RequestBody UserProfileRequest profileRequest,  @CurrentUser UserPrincipal userPrincipal) {
         userService.updateUserDetails(profileRequest, userPrincipal.getEmail());
        return ResponseEntity.ok(new ApiResponse(true, "User data successfully updated"));
    }
}

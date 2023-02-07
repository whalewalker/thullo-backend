package com.thullo.web.controller;


import com.thullo.annotation.CurrentUser;
import com.thullo.security.UserPrincipal;
import com.thullo.service.UserService;
import com.thullo.web.exception.UserException;
import com.thullo.web.payload.request.UserProfileRequest;
import com.thullo.web.payload.response.ResponseDTO;
import com.thullo.web.payload.response.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("api/v1/thullo/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<ResponseDTO> getUserDetails(@CurrentUser UserPrincipal userPrincipal) {
        try {
            UserProfileResponse userDetails = userService.getUserDetails(userPrincipal.getEmail());
            return ResponseEntity.ok(new ResponseDTO(
                    true, "User data successfully retrieved", userDetails));
        } catch (UserException ex) {
            return ResponseEntity.badRequest().body(new ResponseDTO(false, "Bad request, check your request data"));
        }

    }

    @PostMapping("/edit")
    public ResponseEntity<ResponseDTO> updateUserDetails(@RequestBody UserProfileRequest profileRequest, @CurrentUser UserPrincipal userPrincipal) {
        try {
            userService.updateUserDetails(profileRequest, userPrincipal.getEmail());
            return ResponseEntity.ok(new ResponseDTO(true, "User data successfully updated"));
        } catch (UserException ex) {
            return ResponseEntity.badRequest().body(new ResponseDTO(false, "Bad request, check your request data"));
        }

    }
}

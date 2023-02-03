package com.thullo.web.controller;

import com.thullo.data.model.Token;
import com.thullo.data.model.User;
import com.thullo.service.AuthService;
import com.thullo.web.exception.AuthException;
import com.thullo.web.exception.TokenException;
import com.thullo.web.exception.UserException;
import com.thullo.web.payload.request.LoginRequest;
import com.thullo.web.payload.request.PasswordRequest;
import com.thullo.web.payload.request.TokenRefreshRequest;
import com.thullo.web.payload.request.UserRequest;
import com.thullo.web.payload.response.ApiResponse;
import com.thullo.web.payload.response.JwtTokenResponse;
import com.thullo.web.payload.response.TokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

import static com.thullo.data.model.TokenType.VERIFICATION;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@Slf4j
@RequestMapping("api/v1/thullo/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRequest userRequest, HttpServletRequest request) {
        try {
            User user = authService.registerNewUserAccount(userRequest);
            String token = UUID.randomUUID().toString();
            Token vToken = authService.createVerificationToken(user, token, VERIFICATION.toString());

            ResponseEntity<ApiResponse> methodLinkBuilder = methodOn(AuthController.class)
                    .verifyUser(vToken.getToken());

            Link verificationLink = linkTo(methodLinkBuilder).withRel("user-verification");

            user.add(verificationLink);

            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (AuthException | UnsupportedEncodingException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/verify-token")
    public ResponseEntity<ApiResponse> verifyUser(@RequestParam("token") String token) {
        try {
            authService.confirmVerificationToken(token);
            return new ResponseEntity<>(new ApiResponse
                    (true, "User is successfully verified"), HttpStatus.OK);
        } catch (TokenException e) {
            return new ResponseEntity<>(new ApiResponse
                    (false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            JwtTokenResponse authenticationDetail = authService.login(loginRequest);
            return new ResponseEntity<>(new ApiResponse(true, "User is successfully logged in",
                    authenticationDetail ), HttpStatus.OK);
        }catch (UserException e){
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/password/reset-token")
    public ResponseEntity<?> getResetPassword(@RequestParam("email") String email) {
        try {
            TokenResponse passwordResetToken = authService.createPasswordResetTokenForUser(email);
            return new ResponseEntity<>(passwordResetToken, HttpStatus.CREATED);
        } catch (AuthException e) {
            return new ResponseEntity<>(new ApiResponse
                    (false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/password/reset")
    public ResponseEntity<ApiResponse> updatePassword(@Valid @RequestBody PasswordRequest passwordRequest) {
        try {
            authService.saveResetPassword(passwordRequest);
            return new ResponseEntity<>(new ApiResponse
                    (true, "User password is successfully updated"), HttpStatus.OK);
        } catch (AuthException | TokenException e) {
            return new ResponseEntity<>(new ApiResponse
                    (false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }



    @PostMapping("/verification/resend-token")
    public ResponseEntity<?> resendVerificationToken(@RequestParam("token") String token) {
        try {
            Token vToken = authService.resendVerificationToken(token);
            ResponseEntity<ApiResponse> methodLinkBuilder = methodOn(AuthController.class)
                    .verifyUser(vToken.getToken());

            Link verificationLink = linkTo(methodLinkBuilder).withRel("user-verification");

            vToken.add(verificationLink);

            return new ResponseEntity<>(vToken, HttpStatus.OK);
        } catch (TokenException e) {
            return new ResponseEntity<>(new ApiResponse
                    (false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("password/reset/resend-token")
    public ResponseEntity<ApiResponse> resendResetPasswordToken(@RequestParam("token") String token) {
        try {
            Token vToken = authService.resendResetPasswordToken(token);
            PasswordRequest passwordRequest = new PasswordRequest();
            passwordRequest.setToken(vToken.getToken());

            ResponseEntity<ApiResponse> methodLinkBuilder = methodOn(AuthController.class)
                    .updatePassword(passwordRequest);

            Link verificationLink = linkTo(methodLinkBuilder).withRel("password-token");

            vToken.add(verificationLink);

            return new ResponseEntity<>(new ApiResponse
                    (true, "A new reset password token is successfully sent to your email address"), HttpStatus.OK);
        } catch (TokenException e) {
            return new ResponseEntity<>(new ApiResponse
                    (false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request, HttpServletRequest httpServletRequest) {
        try {
            JwtTokenResponse jwtTokenResponse = authService.refreshToken(request);
            return new ResponseEntity<>(jwtTokenResponse, HttpStatus.OK);
        } catch (TokenException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

}

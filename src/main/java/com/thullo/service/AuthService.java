package com.thullo.service;


import com.thullo.data.model.Token;
import com.thullo.data.model.User;
import com.thullo.web.exception.AuthException;
import com.thullo.web.exception.TokenException;
import com.thullo.web.exception.UserException;
import com.thullo.web.payload.request.LoginRequest;
import com.thullo.web.payload.request.PasswordRequest;
import com.thullo.web.payload.request.TokenRefreshRequest;
import com.thullo.web.payload.request.UserRequest;
import com.thullo.web.payload.response.JwtTokenResponse;
import com.thullo.web.payload.response.TokenResponse;

import java.io.UnsupportedEncodingException;

public interface AuthService {
    User registerNewUserAccount(UserRequest userRequest) throws AuthException,  UnsupportedEncodingException;
    void confirmVerificationToken(String verificationToken) throws TokenException;
    Token createVerificationToken(User user, String token, String tokenType);
    JwtTokenResponse login(LoginRequest loginRequest) throws UserException;
    void saveResetPassword(PasswordRequest passwordRequest) throws AuthException, TokenException;
    TokenResponse resendVerificationToken(String token) throws TokenException;
    TokenResponse resendResetPasswordToken(String token) throws TokenException;
    TokenResponse createPasswordResetTokenForUser(String email) throws AuthException;
    JwtTokenResponse refreshToken(TokenRefreshRequest request) throws TokenException;
}

package com.thullo.service;


import com.thullo.data.model.AuthProvider;
import com.thullo.data.model.Token;
import com.thullo.data.model.User;
import com.thullo.data.repository.RoleRepository;
import com.thullo.data.repository.TokenRepository;
import com.thullo.data.repository.UserRepository;
import com.thullo.security.JwtTokenProvider;
import com.thullo.web.exception.AuthException;
import com.thullo.web.exception.TokenException;
import com.thullo.web.exception.UserException;
import com.thullo.web.payload.request.LoginRequest;
import com.thullo.web.payload.request.PasswordRequest;
import com.thullo.web.payload.request.TokenRefreshRequest;
import com.thullo.web.payload.request.UserRequest;
import com.thullo.web.payload.response.JwtTokenResponse;
import com.thullo.web.payload.response.TokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.thullo.data.model.TokenType.*;
import static com.thullo.util.Helper.isNullOrEmpty;
import static com.thullo.util.Helper.isValidToken;
import static java.lang.String.format;


@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final TokenRepository tokenRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;


    @Override
    @Transactional
    public User registerNewUserAccount(UserRequest userRequest) throws AuthException {
        if (validateEmail(userRequest.getEmail())) {
            throw new AuthException("Email is already in use");
        }
        User user = modelMapper.map(userRequest, User.class);
        user.setProvider(AuthProvider.LOCAL);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(List.of(roleRepository.findByName("ROLE_USER").get()));
        return saveAUser(user);
    }

    private boolean validateEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private User saveAUser(User user) {
        return userRepository.save(user);
    }


    @Override
    public void confirmVerificationToken(String verificationToken) throws TokenException {
        Token vToken = getAToken(verificationToken, VERIFICATION.toString());

        if (isValidToken(vToken.getExpiryDate()))
            throw new TokenException("Token has expired");

        User user = vToken.getUser();
        user.setEmailVerified(true);
        saveAUser(user);
        tokenRepository.delete(vToken);
    }

    @Override
    public Token createVerificationToken(User user, String token, String tokenType) {
        Token verificationToken = new Token(token, user, tokenType);
        return tokenRepository.save(verificationToken);
    }

    private Token getAToken(String token, String tokenType) throws TokenException {
        return tokenRepository.findByTokenAndTokenType(token, tokenType)
                .orElseThrow(() -> new TokenException("Invalid token"));
    }


    @Transactional
    @Override
    public JwtTokenResponse login(LoginRequest loginRequest) throws UserException {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final String jwtToken = tokenProvider.generateToken(loginRequest.getEmail());
        User user = internalFindUserByEmail(loginRequest.getEmail());
        Token refreshToken = new Token(user);
        tokenRepository.save(refreshToken);
        return new JwtTokenResponse(jwtToken, refreshToken.getToken(), user.getEmail());
    }

    private User internalFindUserByEmail(String email) throws UserException {
        return userRepository.findByEmail(email).orElseThrow(()-> new UserException(format( "user not found with email %s", email)));
    }

    @Override
    public Token resendVerificationToken(String token) throws TokenException {
        return generateNewToken(token, VERIFICATION.toString());
    }

    @Override
    public Token resendResetPasswordToken(String verificationToken) throws TokenException {
        return generateNewToken(verificationToken, PASSWORD_RESET.toString());
    }


    private Token generateNewToken(String token, String tokenType) throws TokenException {
        Token vCode = getAToken(token, tokenType);
        vCode.updateToken(UUID.randomUUID().toString(), tokenType);
        return tokenRepository.save(vCode);
    }

    @Override
    public TokenResponse createPasswordResetTokenForUser(String email) throws AuthException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("No user found with email " + email));
        String token = UUID.randomUUID().toString();
        Token createdToken = createVerificationToken(user, token, PASSWORD_RESET.toString());
        return modelMapper.map(createdToken, TokenResponse.class);
    }

    @Override
    public JwtTokenResponse refreshToken(TokenRefreshRequest request) throws TokenException {
        String requestRefreshToken = request.getRefreshToken();
        Optional<Token> refreshToken = tokenRepository.findByTokenAndTokenType(requestRefreshToken, REFRESH.toString());
        if (refreshToken.isPresent()) {
            Token token = getRefreshToken(refreshToken.get());
            String jwtToken = tokenProvider.generateToken(refreshToken.get().getUser().getEmail());
            return new JwtTokenResponse(jwtToken, requestRefreshToken, token.getUser().getEmail());
        } else throw new TokenException("Invalid refresh token");
    }

    private Token getRefreshToken(Token token) throws TokenException {
        if (!isValidToken(token.getExpiryDate()))
            return token;
        else throw new TokenException("Refresh token was expired. Please make a new sign in request");
    }



    @Override
    public void saveResetPassword(PasswordRequest request) throws TokenException, AuthException {
        if (isNullOrEmpty(request.getToken())) throw new AuthException("Token must cannot be blank");
        Token pToken = getAToken(request.getToken(), PASSWORD_RESET.toString());
        User userToChangePassword = pToken.getUser();
        userToChangePassword.setPassword(passwordEncoder.encode(request.getPassword()));
        saveAUser(userToChangePassword);
        tokenRepository.delete(pToken);
    }
}

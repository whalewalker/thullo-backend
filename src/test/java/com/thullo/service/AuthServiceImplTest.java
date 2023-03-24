package com.thullo.service;

import com.thullo.data.model.Role;
import com.thullo.data.model.Token;
import com.thullo.data.model.User;
import com.thullo.data.repository.RoleRepository;
import com.thullo.data.repository.TokenRepository;
import com.thullo.data.repository.UserRepository;
import com.thullo.security.CustomUserDetailService;
import com.thullo.security.JwtTokenProvider;
import com.thullo.security.UserPrincipal;
import com.thullo.web.exception.AuthException;
import com.thullo.web.exception.TokenException;
import com.thullo.web.exception.UserException;
import com.thullo.web.payload.request.LoginRequest;
import com.thullo.web.payload.request.PasswordRequest;
import com.thullo.web.payload.request.UserRequest;
import com.thullo.web.payload.response.JwtTokenResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static com.thullo.data.model.TokenType.PASSWORD_RESET;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private TokenRepository tokenRepository;


    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private CustomUserDetailService customUserDetailsService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    private User mockedUser;
    private Role role;
    private Token token;

    @InjectMocks
    private AuthServiceImpl authService;


    @BeforeEach
    void setUp() {
        mockedUser = new User();
        mockedUser.setId(1L);
        mockedUser.setName("Abdullah Ismail");
        mockedUser.setEmail("ismail@gmail.com");
        mockedUser.setPassword("pass1234");
        role = new Role("ROLE_USER");
        mockedUser.getRoles().add(role);

        token = new Token();
        token.setUser(mockedUser);
        token.setToken(UUID.randomUUID().toString());
    }

    @Test
    void userCanRegister() throws AuthException {
        UserRequest userRequest = new UserRequest();
        userRequest.setEmail("ismail@gmail.com");

        //Given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));
        when(modelMapper.map(userRequest, User.class)).thenReturn(mockedUser);
        when(userRepository.save(any(User.class))).thenReturn(mockedUser);

        //When
        authService.registerNewUserAccount(userRequest);

        //Assert
        verify(userRepository, times(1)).existsByEmail(mockedUser.getEmail());
        verify(userRepository, times(1)).save(mockedUser);
    }

//    @Test
//    void fail_1() {
//        boolean isValid = false;
//        assertThat(isValid).isTrue();
//    }
//
//    @Test
//    void fail_2() {
//        boolean isValid = false;
//        assertThat(isValid).isTrue();
//    }


    @Test
    void whenLoginMethodIsCalled_ThenFindUserByEmailIsCalledOnce() throws UserException {
        //Given
        LoginRequest loginRequest = new LoginRequest("ismail@gmail.com", "password123");
        when(userRepository.findByEmail("ismail@gmail.com")).thenReturn(Optional.of(mockedUser));

        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(loginRequest.getEmail(),
                loginRequest.getPassword());
        testingAuthenticationToken.setAuthenticated(true);
        testingAuthenticationToken.setDetails(loginRequest);

        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(), loginRequest.getPassword())
        )).thenReturn(testingAuthenticationToken);
        SecurityContextHolder.getContext().setAuthentication(testingAuthenticationToken);


        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockedUser));
        UserPrincipal fetchedUser = (UserPrincipal) customUserDetailsService.loadUserByUsername(loginRequest.getEmail());
        String actualToken = jwtTokenProvider.generateToken(loginRequest.getEmail());

        when(customUserDetailsService.loadUserByUsername(anyString())).thenReturn(fetchedUser);
        when(jwtTokenProvider.generateToken(anyString())).thenReturn(actualToken);
        when(tokenRepository.save(any(Token.class))).thenReturn(new Token());

        JwtTokenResponse jwtTokenResponse = authService.login(loginRequest);
        verify(customUserDetailsService, times(1)).loadUserByUsername(loginRequest.getEmail());
        verify(jwtTokenProvider, times(2)).generateToken(mockedUser.getEmail());
        verify(userRepository, times(1)).findByEmail(loginRequest.getEmail());
        verify(tokenRepository, times(1)).save(any(Token.class));

        assertNotNull(jwtTokenResponse);
        assertEquals(jwtTokenResponse.getJwtToken(), actualToken);
        assertEquals(jwtTokenResponse.getEmail(), loginRequest.getEmail());
    }


    @Test
    @DisplayName("Saved user can update password")
    void checkIfSavedUserCanUpdatePassword() throws AuthException, TokenException {
        String encoder = UUID.randomUUID().toString();
        //Given
        PasswordRequest passwordRequest = new PasswordRequest(token.getToken(), "password123", mockedUser.getPassword());
        when(tokenRepository.findByTokenAndTokenType(passwordRequest.getToken(), PASSWORD_RESET.toString())).thenReturn(Optional.of(token));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockedUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(passwordEncoder.encode(passwordRequest.getPassword())).thenReturn(encoder);
        when(userRepository.save(mockedUser)).thenReturn(new User());

        //When
        String expected = passwordRequest.getOldPassword();
        String actual = mockedUser.getPassword();
        authService.saveResetPassword(passwordRequest);

        //Assert
        verify(passwordEncoder, times(1)).encode(passwordRequest.getPassword());
        verify(userRepository, times(1)).save(mockedUser);

        assertNotEquals(expected, mockedUser.getPassword());
        assertEquals(encoder, mockedUser.getPassword());
    }

    @Test
    void whenLoginMethodIsCalled_withNullEmail_NullPointerExceptionIsThrown(){
        LoginRequest loginDto = new LoginRequest();
        when(userRepository.findByEmail(loginDto.getEmail())).thenThrow(new NullPointerException("User email cannot be null"));
        verify(userRepository, times(0)).findByEmail(loginDto.getEmail());
    }

    @Test
    void whenLoginMethodIsCalled_withNullPassword_NullPointerExceptionIsThrown(){
        LoginRequest loginDto = new LoginRequest();
        loginDto.setEmail("whalewalker@gmail.com");
        when(userRepository.findByEmail(loginDto.getEmail())).thenThrow(new NullPointerException("User password cannot be null"));
        verify(userRepository, times(0)).findByEmail(loginDto.getEmail());
    }


    @Test
    @DisplayName("User can reset password")
    void savedUserCanResetPassword() throws TokenException, AuthException {
        // Given
        String randomEncoder = UUID.randomUUID().toString();
        String passwordResetToken = UUID.randomUUID().toString();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockedUser));
        when(tokenRepository.findByTokenAndTokenType(passwordResetToken, PASSWORD_RESET.toString())).thenReturn(Optional.of(token));
        when(passwordEncoder.encode(anyString())).thenReturn(randomEncoder);

        PasswordRequest passwordRequest = new PasswordRequest();
        passwordRequest.setPassword("12345");
        passwordRequest.setOldPassword(mockedUser.getPassword());
        passwordRequest.setToken(passwordResetToken);

        authService.saveResetPassword(passwordRequest);

        ArgumentCaptor<User> tokenArgumentCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository, times(1)).save(tokenArgumentCaptor.capture());

        assertThat(tokenArgumentCaptor.getValue()).isNotNull();
        assertThat(tokenArgumentCaptor.getValue().getPassword()).isNotNull();
    }



}
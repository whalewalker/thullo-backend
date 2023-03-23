//package com.thullo.security;
//
//import com.thullo.data.model.AuthProvider;
//import com.thullo.data.model.Privilege;
//import com.thullo.data.model.Role;
//import com.thullo.data.model.User;
//import com.thullo.data.repository.UserRepository;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.mockito.junit.jupiter.MockitoSettings;
//import org.mockito.quality.Strictness;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//@MockitoSettings(strictness = Strictness.LENIENT)
//class SecurityConfigTest {
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private JwtTokenProvider tokenProvider;
//
//    @InjectMocks
//    private CustomUserDetailService customUserDetailsService;
//
//    private User mockedUser;
//
//    @BeforeEach
//    void setUp() {
//        mockedUser = new User();
//        mockedUser.setName("Abdullah Ismail");
//        mockedUser.setEmail("test@gmail.com");
//        mockedUser.setProvider(AuthProvider.LOCAL);
//        mockedUser.setPassword("pass1234");
//        Role role = new Role("ROLE_USER");
//        role.setPrivileges(List.of(new Privilege()));
//        mockedUser.getRoles().add(role);
//
//    }
//
//    @AfterEach
//    void tearDown() {
//        userRepository = null;
//        customUserDetailsService = null;
//    }
//
//    /**
//     * User details service test
//     */
//
//    @Test
//    @DisplayName("User details can be fetch from database by email with role User")
//    void user_canFetchDataFromDbByEmail() {
//        when(userRepository.findByEmail(mockedUser.getEmail()))
//                .thenReturn(Optional.of(mockedUser));
//
//        UserPrincipal mockedPrincipal = new UserPrincipal(1L, "test", "test@gmail.com", "1234", true, Collections.emptyList());
//        when(UserPrincipal.create(mockedUser)).thenReturn(mockedPrincipal);
//
//        UserPrincipal fetchedUser = (UserPrincipal) customUserDetailsService.loadUserByUsername(mockedUser.getEmail());
//
//        verify(userRepository, times(1)).findByEmail(mockedUser.getEmail());
//
//        assertNotNull(fetchedUser);
//        assertAll(
//                () -> assertEquals(mockedUser.getName(), fetchedUser.getName()),
//                () -> assertEquals(mockedUser.getEmail(), fetchedUser.getEmail()),
//                () -> assertEquals(mockedUser.getPassword(), fetchedUser.getPassword()),
//                () -> assertEquals(1L, fetchedUser.getAuthorities().size())
//        );
//    }
//
//
//    /**
//     * Jwt Token Test
//     */
//
//    @Test
//    @DisplayName("Jwt token can be generated")
//    void jwt_tokenCanBeGenerated() {
//        //Given
//        when(userRepository.findByEmail(mockedUser.getEmail()))
//                .thenReturn(Optional.of(mockedUser));
//        when(tokenProvider.generateToken(any())).thenReturn(UUID.randomUUID().toString());
//
//        //When
//        String actualToken = tokenProvider.generateToken(mockedUser.getEmail());
//
//        //Assert
//        assertNotNull(actualToken);
//        assertEquals(actualToken.getClass(), String.class);
//    }
//
//    @Test
//    @DisplayName("Username can be extracted from jwt token")
//    void can_extractUsernameFromJwtToken() {
//        //Given
//        when(userRepository.findByEmail(mockedUser.getEmail()))
//                .thenReturn(Optional.of(mockedUser));
//        when(tokenProvider.generateToken(any())).thenReturn(UUID.randomUUID().toString());
//        when(tokenProvider.extractEmail(anyString())).thenReturn("Abdullah Ismail");
//        //When
//        String jwtToken = tokenProvider.generateToken(mockedUser.getEmail());
//        String actual = tokenProvider.extractEmail(jwtToken);
//
//        //Assert
//        assertEquals(mockedUser.getName(), actual);
//    }
//
//    @Test
//    @DisplayName("Token can be validated by checking expiration date")
//    void test_thatTokenHasNotExpire() {
//        //Given
//        when(userRepository.findByEmail(mockedUser.getEmail()))
//                .thenReturn(Optional.of(mockedUser));
//
//        //When
//        String jwtToken = tokenProvider.generateToken(mockedUser.getEmail());
//        boolean hasExpire = tokenProvider.isTokenExpired(jwtToken);
//
//        //Assert
//        assertFalse(hasExpire);
//    }
//
//    @Test
//    @DisplayName("Jwt token can be validated by username and expiration date")
//    void test_jwtTokenCanBeValidated() {
//        //Given
//        when(userRepository.findByEmail(mockedUser.getEmail()))
//                .thenReturn(Optional.of(mockedUser));
//
//        //When
//        UserPrincipal fetchedUser = (UserPrincipal) customUserDetailsService.loadUserByUsername(mockedUser.getEmail());
//        String jwtToken = tokenProvider.generateToken(mockedUser.getEmail());
//        boolean isValid = tokenProvider.validateToken(jwtToken, fetchedUser);
//
//        //Assert
//        assertFalse(isValid);
//    }
//
//}
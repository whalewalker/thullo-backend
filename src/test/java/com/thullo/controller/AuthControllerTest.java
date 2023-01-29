package com.thullo.controller;

import com.thullo.data.model.Token;
import com.thullo.data.model.User;
import com.thullo.data.repository.UserRepository;
import com.thullo.security.CustomUserDetailService;
import com.thullo.service.AuthService;
import com.thullo.web.controller.AuthController;
import com.thullo.web.payload.request.UserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static com.thullo.data.model.TokenType.VERIFICATION;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(SpringExtension.class)
@AutoConfigureJsonTesters
@WebMvcTest(AuthController.class)
@Import({CustomUserDetailService.class, UserRepository.class })
class AuthControllerTest {
    @MockBean
    private AuthService authService;

    MockMvc mockMvc;

    @InjectMocks
    AuthController authController;

    @Autowired
    private JacksonTester<UserRequest> userRequestJson;

    User user;

    UserRequest userRequest;

    Token token;
    String randomToken = UUID.randomUUID().toString();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        user = new User();
        user.setId(1L);
        user.setEmail("test@gmail.com");
        user.setName("Ismail Abdullah");

        userRequest = new UserRequest();
        userRequest.setEmail("Ismail Abdullah");
        userRequest.setEmail("test@gmail.com");

        token = new Token();
        token.setToken(randomToken);
        token.setUser(user);
    }

    @Test
    void registerUserWithValidData() throws Exception {
        given(authService.registerNewUserAccount(eq(userRequest)))
                .willReturn(user);

        given(authService.createVerificationToken(eq(user), eq(randomToken), VERIFICATION.name()))
                .willReturn(token);

        MockHttpServletResponse response = mockMvc.perform(
                post("/api/v1/thullo/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(userRequestJson.write(userRequest).getJson())
        ).andReturn().getResponse();

        then(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }
}
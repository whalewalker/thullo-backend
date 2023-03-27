package com.thullo.controller;

import com.thullo.data.model.Token;
import com.thullo.data.model.User;
import com.thullo.service.AuthServiceImpl;
import com.thullo.web.payload.request.UserRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static com.thullo.data.model.TokenType.VERIFICATION;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

//@ExtendWith(SpringExtension.class)
//@AutoConfigureJsonTesters
//@WebMvcTest(AuthController.class)
//@Import({CustomUserDetailService.class, UserRepository.class })

@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthControllerTest {
    @MockBean
    private AuthServiceImpl authService;

    private MockMvc mockMvc;

    private JacksonTester<UserRequest> userRequestJson;

    User user;

    UserRequest userRequest;

    Token token;
    String randomToken = UUID.randomUUID().toString();
    @Autowired
    private WebApplicationContext applicationContext;
    @BeforeAll
    public void init(){
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .build();
    }

    @BeforeEach
    void setUp() {

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

package com.thullo.controller;


import com.thullo.service.UserService;
import com.thullo.web.controller.UserController;
import com.thullo.web.payload.response.UserProfileResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@ExtendWith(SpringExtension.class)
@AutoConfigureJsonTesters
@WebMvcTest(UserController.class)
class UserControllerTest {

    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JacksonTester<UserProfileResponse> userDetails;
    private UserProfileResponse userProfile;
    @BeforeEach
    void setUp() {
        userProfile = new UserProfileResponse();
        userProfile.setEmail("ismail@gmail.com");
        userProfile.setName("Ismail Abdullah");
    }

    @Test
    void getUserDetails() throws Exception{
        given(userService.getUserDetails(eq("ismail@gmail.com")))
                .willReturn(userProfile);

        MockHttpServletResponse response = mockMvc.perform(
                get("api/v1/thullo/user")
        ).andReturn().getResponse();

        then(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        then(response.getContentAsString()).isEqualTo(userDetails.write(userProfile).getJson());
    }
}
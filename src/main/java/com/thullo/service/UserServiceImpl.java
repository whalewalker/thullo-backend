package com.thullo.service;


import com.thullo.data.model.User;
import com.thullo.data.repository.UserRepository;
import com.thullo.web.payload.request.UserProfileRequest;
import com.thullo.web.payload.response.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper mapper;

    /**
     * Retrieves user information from the database and returns it as a UserProfileResponse object.
     *@param email The unique identifier for the user whose information is being retrieved.
     * @return UserProfileResponse object that encapsulates various details such as name, email, address, and any other relevant information related to the user.
     */
    @Override
    public UserProfileResponse getUserDetails(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        UserProfileResponse userDetail = mapper.map(optionalUser.get(), UserProfileResponse.class);
        return userDetail;
    }

    /**
     * Updates the user details for a given email.
     *
     * @param userRequest - An object containing the updated information for the user profile
     * @param email       - The email of the user whose profile needs to be updated
     */
    @Override
    public void updateUserDetails(UserProfileRequest userRequest, String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        mapper.map(userRequest, optionalUser.get());
        userRepository.save(optionalUser.get());
    }
}

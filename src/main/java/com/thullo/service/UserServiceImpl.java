package com.thullo.service;


import com.thullo.data.model.User;
import com.thullo.data.repository.UserRepository;
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
}

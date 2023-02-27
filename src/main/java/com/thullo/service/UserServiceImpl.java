package com.thullo.service;


import com.thullo.data.model.User;
import com.thullo.data.repository.UserRepository;
import com.thullo.web.exception.UserException;
import com.thullo.web.payload.request.UserProfileRequest;
import com.thullo.web.payload.response.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper mapper;

    /**
     * Retrieves user information from the database and returns it as a User object.
     *@param email The unique identifier for the user whose information is being retrieved.
     * @return User object that encapsulates various details such as name, email, address, and any other relevant information related to the user.
     */
    @Override
    public UserProfileResponse getUserDetails(String email) throws UserException {
        User user = internalFindUserByEmail(email);
        return mapper.map(user, UserProfileResponse.class);
    }

    /**
     * Updates the user details for a given email.
     *
     * @param userRequest - An object containing the updated information for the user profile
     * @param email       - The email of the user whose profile needs to be updated
     */
    @Override
    public void updateUserDetails(UserProfileRequest userRequest, String email) throws UserException {
        User user = internalFindUserByEmail(email);
        mapper.map(userRequest, user);
        userRepository.save(user);
    }

    /**
     * Searches for user profiles by email or name.
     *
     * @param searchQuery A string containing either the email or name to search for.
     * @return A list of UserProfileResponse objects that match the search criteria.
     */
    @Override
    public List<UserProfileResponse> searchUserProfiles(String searchQuery) {
        List<User> users = userRepository.findByParams(searchQuery);
        List<UserProfileResponse> profiles = new ArrayList<>();
        users.forEach(user -> {
            UserProfileResponse profile = mapper.map(user, UserProfileResponse.class);
            profiles.add(profile);
        });
        return profiles;
    }


    private User internalFindUserByEmail(String email) throws UserException {
        return userRepository.findByEmail(email).orElseThrow(() -> new UserException(format("user not found with email %s", email)));
    }
}

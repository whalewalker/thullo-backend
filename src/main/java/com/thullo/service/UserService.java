package com.thullo.service;


import com.thullo.web.exception.UserException;
import com.thullo.web.payload.request.UserProfileRequest;
import com.thullo.web.payload.response.UserResponse;

import java.util.List;

/**
 This class provides a centralized implementation for accessing and managing user information, offering methods for retrieving various details such as name, email, address, and updating information as needed.
 */
public interface UserService {

    /**
     * Retrieves user information from the database and returns it as a UserProfileResponse object.
     *
     * @param email The unique identifier for the user whose information is being retrieved.
     * @return UserProfileResponse object that encapsulates various details such as name, email, address, and any other relevant information related to the user.
     */
    UserResponse getUserDetails(String email) throws UserException;

    /**
     * Updates the user details for a given email.
     *
     * @param userRequest - An object containing the updated information for the user profile
     * @param email       - The email of the user whose profile needs to be updated
     */
    void updateUserDetails(UserProfileRequest userRequest, String email) throws UserException;

    /**
     * Searches for user profiles by email or name.
     *
     * @param searchQuery A string containing either the email or name to search for.
     * @return A list of UserProfileResponse objects that match the search criteria.
     */
    List<UserResponse> searchUserProfiles(String searchQuery);
}

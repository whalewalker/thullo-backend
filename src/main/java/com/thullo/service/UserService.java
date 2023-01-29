package com.thullo.service;


import com.thullo.web.payload.response.UserProfileResponse;

/**
 This class provides a centralized implementation for accessing and managing user information, offering methods for retrieving various details such as name, email, address, and updating information as needed.
 */
public interface UserService {

    /**
     * Retrieves user information from the database and returns it as a UserProfileResponse object.
     * @param email The unique identifier for the user whose information is being retrieved.
     * @return UserProfileResponse object that encapsulates various details such as name, email, address, and any other relevant information related to the user.
     */
    UserProfileResponse getUserDetails(String email);
}

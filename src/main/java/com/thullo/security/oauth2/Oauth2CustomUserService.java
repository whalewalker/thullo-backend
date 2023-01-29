package com.thullo.security.oauth2;

import com.thullo.data.model.AuthProvider;
import com.thullo.data.model.User;
import com.thullo.data.repository.RoleRepository;
import com.thullo.data.repository.UserRepository;
import com.thullo.security.UserPrincipal;
import com.thullo.security.oauth2.user.Oauth2UserData;
import com.thullo.web.exception.OAuth2AuthenticationProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.thullo.security.oauth2.user.OAuth2UserDataFactory.getOauth2UserData;


@Service
@RequiredArgsConstructor
public class Oauth2CustomUserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        try {
            return processOAuth2User(userRequest, oAuth2User);
        }catch (Exception ex){
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        Oauth2UserData oauth2UserData = getOauth2UserData(userRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());

        if (oauth2UserData != null && oauth2UserData.getEmail().isEmpty()) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }

        Optional<User> optionalUser = userRepository.findByEmail(oauth2UserData.getEmail());
        User user;
        if (optionalUser.isPresent()){
             user = optionalUser.get();
            if (!user.getProvider().equals(AuthProvider.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase()))) {
                throw new OAuth2AuthenticationProcessingException("Looks like you're signed up with " +
                        user.getProvider().name().toUpperCase() + " account. Please use your " + user.getProvider().name().toUpperCase() +
                        " account to login.");
            }
            user = updateExistingUser(user, oauth2UserData);
        }else {
            user = registerNewUser(oauth2UserData, userRequest);
        }

        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }

    private User registerNewUser(Oauth2UserData oauth2UserData, OAuth2UserRequest userRequest) {
        User userToSave = new User();
        userToSave.setEmailVerified(true);
        userToSave.setEmail(oauth2UserData.getEmail());
        userToSave.setImageUrl(oauth2UserData.getImageUrl());
        userToSave.setName(oauth2UserData.getName());
        userToSave.setProvider(AuthProvider.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase()));
        userToSave.setProviderId(oauth2UserData.getUserId());
        userToSave.setRoles(List.of(roleRepository.findByName("ROLE_USER").get()));
        return userRepository.save(userToSave);
    }

    private User updateExistingUser(User existingUser, Oauth2UserData oauth2UserData) {
        existingUser.setName(oauth2UserData.getName());
        existingUser.setImageUrl(oauth2UserData.getImageUrl());
        return userRepository.save(existingUser);
    }


}

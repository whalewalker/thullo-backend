package com.thullo.security.oauth2.user;

import java.util.Map;

import static com.thullo.data.model.AuthProvider.*;

public class OAuth2UserDataFactory {

    private OAuth2UserDataFactory() {}

    public static Oauth2UserData getOauth2UserData(String registrationId, Map<String, Object> attributes){
        if (registrationId.equalsIgnoreCase(GOOGLE.name()))
            return new GoogleOauth2UserData(attributes);
        if (registrationId.equalsIgnoreCase(GITHUB.name()))
            return new GithubOauth2UserData(attributes);
        if(registrationId.equalsIgnoreCase(FACEBOOK.name()))
            return new FacebookOauth2UserData(attributes);
        if (registrationId.equalsIgnoreCase(TWITTER.name())){
            return new TwitterOauth2UserData(attributes);
        }
        return null;
    }
}

package com.thullo.security.oauth2.user;

import java.util.Map;

public class TwitterOauth2UserData extends Oauth2UserData{

    public TwitterOauth2UserData(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getUserId() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getEmail() {
        return null;
    }

    @Override
    public String getImageUrl() {
        return null;
    }
}

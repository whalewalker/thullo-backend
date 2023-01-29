package com.thullo.util;

public interface AppConstants {
    String DEFAULT_PAGE_NUMBER = "0";
    String DEFAULT_PAGE_SIZE = "10";
    int MAX_PAGE_SIZE = 50;
    int JWT_REFRESH_TOKEN_EXPIRATION_IN_HR = 48;
    long EXPIRATION = 48L;
    String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";
    String REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri";

    long MAX_AGE_SECS = 3600;

}

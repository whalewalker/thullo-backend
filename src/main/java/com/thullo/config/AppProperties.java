package com.thullo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 *  This allows to map the entire Properties and Yaml files into an object easily.
 */
@ConfigurationProperties(prefix = "app")
@Data
public class AppProperties {
    private final Auth auth = new Auth();
    private final OAuth2 oAuth2 = new OAuth2();

    @Data
    public static class Auth{
        private String tokenSecret;
        private long tokenExpirationMsec;
    }

    @Data
    public static final class OAuth2{
        private List<String> authorizedRedirectUris = new ArrayList<>();
    }
}

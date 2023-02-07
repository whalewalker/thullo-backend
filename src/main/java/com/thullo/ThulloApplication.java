package com.thullo;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(servers = {@Server(url = "/", description = "Default server URL")})
public class ThulloApplication {
    public static void main(String[] args) {
        SpringApplication.run(ThulloApplication.class, args);
    }

}

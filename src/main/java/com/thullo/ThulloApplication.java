package com.thullo;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(servers = {
        @Server(url = "http://localhost:8080", description = "local"),
        @Server(url = "https://thullo-backend-production.up.railway.app", description = "live")
})
public class ThulloApplication {
    public static void main(String[] args) {
        SpringApplication.run(ThulloApplication.class, args);
    }

}

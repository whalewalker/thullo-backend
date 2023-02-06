package com.thullo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.thullo.data.repository")
public class ThulloApplication {
    public static void main(String[] args) {
        SpringApplication.run(ThulloApplication.class, args);
    }

}

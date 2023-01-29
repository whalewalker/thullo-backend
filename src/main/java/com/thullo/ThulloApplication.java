package com.thullo;

import com.thullo.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class ThulloApplication {

	public static void main(String[] args) {
		SpringApplication.run(ThulloApplication.class, args);
	}

}

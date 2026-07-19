package com.pm.urlshortener;

import com.pm.urlshortener.dto.UrlRequestDto;
import com.pm.urlshortener.dto.UrlResponseDto;
import com.pm.urlshortener.service.UrlService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Bean
    public CommandLineRunner run(UrlService urlService) {
        return args -> {
            System.out.println("=== URL Shortener Application Started ===");
            System.out.println("API Documentation: http://localhost:8080/swagger-ui.html");
            System.out.println("Set app.seed-data.enabled=true in application.properties to load sample data");
            System.out.println("=== URL Shortener is Ready to Accept Requests ===");
        };
    }
}

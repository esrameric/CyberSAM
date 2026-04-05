package com.cybersam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * CyberSAM - Secure Software Asset Management
 * Defense Industry Standard Application
 * 
 * CORS Configuration: Handled globally via CorsConfig.java
 * (implements WebMvcConfigurer for Spring MVC layer)
 */
@SpringBootApplication
public class CyberSAMApplication {
    public static void main(String[] args) {
        SpringApplication.run(CyberSAMApplication.class, args);
    }
}
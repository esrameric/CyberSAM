package com.cybersam.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

/**
 * Global CORS Configuration for CyberSAM API
 * Implements both WebMvcConfigurer for Spring MVC and provides CorsConfigurationSource for Spring Security
 * Enables cross-origin requests from frontend and other trusted sources
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * Configure CORS for Spring MVC layer
     * This handles preflight (OPTIONS) requests and CORS headers for all endpoints
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                    "http://localhost:3000",           // Local development - Frontend
                    "http://localhost:9595",           // Backend Docker port (direct access)
                    "http://localhost",                // Direct localhost access
                    "http://localhost:5173",           // Vite dev server
                    "https://localhost:3000",          // HTTPS local
                    "http://cybersam-frontend",        // Docker container name
                    "https://api.cybersam.defense"     // Production
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")                   // Allow all headers
                .exposedHeaders("Content-Type", "X-Total-Count", "X-Page-Number")
                .allowCredentials(true)               // Allow credentials with specific origins
                .maxAge(3600);                        // Cache preflight for 1 hour
    }

    /**
     * CorsConfigurationSource bean for Spring Security integration (if needed in future)
     * This is an alternative/complementary method to WebMvcConfigurer.addCorsMappings()
     * Kept for compatibility with potential Spring Security configurations
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allow requests from multiple origins (frontend URLs)
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",           // Local development - Frontend
            "http://localhost:9595",           // Backend Docker port (direct access)
            "http://localhost",                // Direct localhost access
            "http://localhost:5173",           // Vite dev server
            "https://localhost:3000",          // HTTPS local
            "http://cybersam-frontend",        // Docker container name
            "https://api.cybersam.defense"     // Production
        ));
        
        // Allow all HTTP methods
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));
        
        // Allow all headers
        configuration.setAllowedHeaders(Arrays.asList(
            "Content-Type", "Authorization", "Accept", "X-Requested-With", "*"
        ));
        
        // Allow credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);
        
        // Cache time for preflight requests
        configuration.setMaxAge(3600L);
        
        // Expose certain headers to client
        configuration.setExposedHeaders(Arrays.asList(
            "Content-Type", "X-Total-Count", "X-Page-Number"
        ));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}

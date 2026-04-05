package com.cybersam.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI Configuration for Swagger 3.0 / Swagger UI
 */
@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server().url("").description("Current Server"),
                        new Server().url("https://api.cybersam.defense").description("Production Server")
                ))
                .info(new Info()
                        .title("CyberSAM API")
                        .version("1.0.0")
                        .description("Secure Software Asset Management (CyberSAM) - Defense Industry Standard REST API\n\n" +
                                "Complete software asset lifecycle management with:\n" +
                                "- License expiry tracking\n" +
                                "- CVE vulnerability monitoring\n" +
                                "- Critical asset identification\n" +
                                "- Compliance reporting\n\n" +
                                "Endpoints are secured and comply with defense industry standards.")
                        .contact(new Contact()
                                .name("CyberSAM Support")
                                .email("support@cybersam.defense")
                                .url("https://www.cybersam.defense"))
                        .license(new License()
                                .name("Defense Industry License")
                                .url("https://www.cybersam.defense/license")));
    }
}
package de.schaffbar.core_pos;

import java.util.List;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
public class SecurityConfiguration {

    @Value("#{'${application.cors.allowed-origins}'.split(',')}")
    private List<String> allowedOrigins;

    @Value("#{'${application.cors.allowed-methods}'.split(',')}")
    private List<String> allowedMethods;

    @Value("#{'${application.cors.allowed-headers}'.split(',')}")
    private List<String> allowedHeaders;

    @Bean
    WebMvcConfigurer corsConfigurer() {

        log.info("Allowed Origins: {}", this.allowedOrigins);
        log.info("Allowed Methods: {}", this.allowedMethods);
        log.info("Allowed Headers: {}", this.allowedHeaders);

        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {

                registry.addMapping("/api/v1/**") //
                        .allowedOrigins(SecurityConfiguration.this.allowedOrigins.toArray(new String[0])) //
                        .allowedMethods(SecurityConfiguration.this.allowedMethods.toArray(new String[0])) //
                        .allowedHeaders(SecurityConfiguration.this.allowedHeaders.toArray(new String[0]));
            }
        };
    }

}

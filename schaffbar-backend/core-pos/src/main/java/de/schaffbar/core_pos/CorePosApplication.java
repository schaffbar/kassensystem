package de.schaffbar.core_pos;

import lombok.NonNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class CorePosApplication {

    public static void main(String[] args) {
        SpringApplication.run(CorePosApplication.class, args);
    }

    // TODO: Remove this CORS configuration in production!
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/api/v1/**") //
                        .allowedOrigins("http://localhost:4200") //
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") //
                ;
            }
        };
    }

}

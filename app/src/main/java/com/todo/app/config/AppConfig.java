package com.todo.app.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class AppConfig {

        @Bean
        public CorsFilter corsFilter() {
                CorsConfiguration config = new CorsConfiguration();

                config.setAllowCredentials(true);
                config.setAllowedOrigins(List.of(
                                "http://localhost:3000",
                                "http://localhost:5173",
                                "http://localhost:4200",
                                "https://toodoo.vercel.app"));
                config.setAllowedHeaders(List.of(
                                "Authorization",
                                "Content-Type",
                                "Accept",
                                "Origin",
                                "X-Requested-With"));
                config.setAllowedMethods(List.of(
                                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
                config.setExposedHeaders(List.of("Authorization"));
                config.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", config);

                return new CorsFilter(source);
        }
}
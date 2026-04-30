package com.itradingsolutions.itex.config.security;

import com.itradingsolutions.itex.api.common.util.services.IHistoryService;
import com.itradingsolutions.itex.config.security.jwt.filter.JWTAuthenticationFilter;
import com.itradingsolutions.itex.config.security.jwt.filter.JWTAuthorizationFilter;
import com.itradingsolutions.itex.config.security.jwt.service.JWTService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final JWTService jwtService;
    private final IHistoryService historyService;
    private final AuthenticationConfiguration authConfig;

    public SecurityConfiguration(JWTService jwtService, IHistoryService historyService, AuthenticationConfiguration authConfig) {
        this.jwtService = jwtService;
        this.historyService = historyService;
        this.authConfig = authConfig;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization", "Content-Disposition", "timeout"));
        configuration.setAllowedOriginPatterns(Arrays.asList("*","http://localhost:4200", "https://localhost"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private static final String[] freePaths = {
            "/websocket"
    };
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorize -> authorize.requestMatchers(freePaths).permitAll().anyRequest().authenticated())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(new JWTAuthenticationFilter(authConfig.getAuthenticationManager(), jwtService, historyService), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new JWTAuthorizationFilter(authConfig.getAuthenticationManager(), jwtService), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}

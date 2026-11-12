package com.example.crudrapido.security;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration 
@EnableWebSecurity 
@RequiredArgsConstructor 
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
             .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/auth/**").permitAll()
                    .requestMatchers("/error").permitAll()
                    .anyRequest().authenticated())

            .exceptionHandling(ex -> ex
                     
                    .authenticationEntryPoint((request, response, authException) -> {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                        response.setCharacterEncoding("UTF-8");
                        response.getWriter().write(
                            "{\"status\":401,\"error\":\"Unauthorized\","
                            + "\"message\":\"Token JWT ausente, invalido o expirado\"}");
                    })
                    // Autenticado pero sin permisos  
                    .accessDeniedHandler((request, response, accessDeniedException) -> {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                        response.setCharacterEncoding("UTF-8");
                        response.getWriter().write(
                            "{\"status\":403,\"error\":\"Forbidden\","
                            + "\"message\":\"No tienes permisos para este recurso\"}");
                    }))

             .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}
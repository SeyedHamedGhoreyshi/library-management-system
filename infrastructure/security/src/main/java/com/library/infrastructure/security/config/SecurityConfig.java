package com.library.infrastructure.security.config;

import com.library.infrastructure.security.filter.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter, AuthenticationProvider authenticationProvider) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth ->
                        auth
                                // LIBRARIAN-only endpoint - must be matched before the
                                // broader "/api/v1/auth/**" permitAll rule below, since
                                // Spring Security applies the first matching rule.
                                .requestMatchers(HttpMethod.POST, "/api/v1/auth/register-librarian")
                                .hasRole("LIBRARIAN")

                                // Public endpoints - authentication
                                .requestMatchers("/api/v1/auth/**")
                                .permitAll()

                                // Public endpoints - Swagger UI / OpenAPI docs
                                .requestMatchers(
                                        "/swagger-ui/**",
                                        "/swagger-ui.html",
                                        "/v3/api-docs/**",
                                        "/v3/api-docs.yaml"
                                )
                                .permitAll()

                                // LIBRARIAN-only endpoints - book management
                                .requestMatchers(HttpMethod.POST, "/api/v1/books")
                                .hasRole("LIBRARIAN")
                                .requestMatchers(HttpMethod.PUT, "/api/v1/books/**")
                                .hasRole("LIBRARIAN")
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/books/**")
                                .hasRole("LIBRARIAN")

                                // USER or LIBRARIAN endpoints - viewing and borrowing
                                .requestMatchers(HttpMethod.GET, "/api/v1/books/**")
                                .hasAnyRole("USER", "LIBRARIAN")
                                .requestMatchers(HttpMethod.POST, "/api/v1/books/**/borrow")
                                .hasAnyRole("USER", "LIBRARIAN")
                                .requestMatchers(HttpMethod.POST, "/api/v1/books/**/return")
                                .hasAnyRole("USER", "LIBRARIAN")

                                // All other requests must be authenticated
                                .anyRequest()
                                .authenticated()
                )
                .sessionManagement(sess ->
                        sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(
                        jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
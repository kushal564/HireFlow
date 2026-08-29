package com.kushal.hireflow.auth;

import com.kushal.hireflow.auth.security.JwtAuthenticationFilter;
import com.kushal.hireflow.auth.security.SecurityExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final SecurityExceptionHandler securityExceptionHandler;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          SecurityExceptionHandler securityExceptionHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.securityExceptionHandler = securityExceptionHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // API authentication uses short-lived Bearer access tokens. The refresh cookie is
                // HttpOnly + SameSite and is accepted only by the refresh/logout endpoints.
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .logout(logout -> logout.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(securityExceptionHandler)
                        .accessDeniedHandler(securityExceptionHandler)
                )
                .authorizeHttpRequests(auth -> auth
                        // Public authentication endpoints
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/api/auth/register",
                                "/api/auth/login",
                                "/api/auth/refresh",
                                "/api/auth/logout",
                                "/api/auth/verify-email",
                                "/api/auth/resend-verification"
                        ).permitAll()

                        // API documentation and health check
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/actuator/health"
                        ).permitAll()

                        // Public read-only resources
                        .requestMatchers(HttpMethod.GET,
                                "/api/companies",
                                "/api/companies/*",
                                "/api/vacancies",
                                "/api/vacancies/*"
                        ).permitAll()

                        // Recruiter-only operations
                        .requestMatchers(HttpMethod.POST, "/api/companies")
                        .hasRole("RECRUITER")
                        .requestMatchers(HttpMethod.PUT, "/api/companies/*")
                        .hasRole("RECRUITER")
                        .requestMatchers(HttpMethod.DELETE, "/api/companies/*")
                        .hasRole("RECRUITER")
                        .requestMatchers(HttpMethod.POST, "/api/vacancies")
                        .hasRole("RECRUITER")
                        .requestMatchers(HttpMethod.PUT, "/api/vacancies/*")
                        .hasRole("RECRUITER")
                        .requestMatchers(HttpMethod.DELETE, "/api/vacancies/*")
                        .hasRole("RECRUITER")
                        .requestMatchers(HttpMethod.GET, "/api/applications/recruiter")
                        .hasRole("RECRUITER")
                        .requestMatchers(HttpMethod.PATCH, "/api/applications/*/status")
                        .hasRole("RECRUITER")

                        // Candidate-only operations
                        .requestMatchers(HttpMethod.POST, "/api/applications")
                        .hasRole("CANDIDATE")
                        .requestMatchers(HttpMethod.GET, "/api/applications/my")
                        .hasRole("CANDIDATE")

                        // Admin-only role management
                        .requestMatchers(HttpMethod.PATCH, "/api/users/*/role")
                        .hasRole("ADMIN")

                        // Everything else requires authentication
                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

package com.projetTransversalIsi.security.application;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
        private final JwtAuthentificationFIlter jwtFilter;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                return http
                                .sessionManagement(
                                                session -> session
                                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(
                                auth -> auth
                                                // Static resources always public
                                                .requestMatchers("/sw.js", "/manifest.json", "/images/**",
                                                                "/css/**", "/js/**", "/webjars/**")
                                                .permitAll()

                                                // Auth endpoints public
                                                .requestMatchers("/login", "/refresh", "/logout")
                                                .permitAll()

                                                // REST API protected (includes /api/sidebar which requires auth)
                                                .requestMatchers("/api/**").authenticated()

                                                // All page routes are now public — auth is enforced
                                                // client-side by sidebar-loader.js (redirects to /login on 401)
                                                .requestMatchers("/**").permitAll()

                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();

        }

}

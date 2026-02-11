package com.argus.config;

import com.argus.security.GoogleSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final GoogleSuccessHandler googleSuccessHandler;

    public SecurityConfig(GoogleSuccessHandler googleSuccessHandler) {
        this.googleSuccessHandler = googleSuccessHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Desabilita CSRF (Padrão para APIs REST com JWT)
                .csrf(csrf -> csrf.disable())

                // Configura quem pode acessar o quê
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login**").permitAll() // Libera a home e login
                        .anyRequest().authenticated() // Bloqueia todo o resto
                )

                // Configura o Login com Google
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(googleSuccessHandler)
                );

        return http.build();
    }
}
package me.krzysztofprogramming.userservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    @Value("${application.api-key.value}")
    private String apiKey;
    @Value("${application.api-key.header}")
    private String apiKeyHeader;

    @Bean
    public PasswordEncoder createPasswordEncoder() {
        return new Argon2PasswordEncoder();
    }

    @Bean
    @Primary
    public SecurityFilterChain createSpringSecurityFilterChain(HttpSecurity http) throws Exception {
        AbstractPreAuthenticatedProcessingFilter authFilter = new ApiKeyAuthFilter(apiKeyHeader);
        authFilter.setAuthenticationManager(this::authenticate);

        return http
                .csrf().disable()
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(authFilter)
                .build();
    }

    private Authentication authenticate(Authentication authentication) {
        String principal = (String) authentication.getPrincipal();
        authentication.setAuthenticated(principal.equals(apiKey));
        log.trace("Principal: {}", principal);
        log.trace("ApiKey: {}", apiKey);
        return authentication;
    }
}

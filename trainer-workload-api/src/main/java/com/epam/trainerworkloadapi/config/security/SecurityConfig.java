package com.epam.trainerworkloadapi.config.security;

import com.epam.trainerworkloadapi.config.security.filter.JWTAuthorizationFilter;
import com.epam.trainerworkloadapi.model.user.KnownAuthority;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${security.jwt.secret}")
    private String secret;

    public SecurityConfig() {
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthenticationManager authManager) throws Exception {
        http.authorizeHttpRequests(authz ->
                        authz
                                // open static resources
                                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                                // open swagger-ui
                                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                                // admin can use Actuator endpoints
                                .requestMatchers(EndpointRequest.toAnyEndpoint()).hasAuthority(KnownAuthority.ROLE_ADMIN.getAuthority())
                                // by default, require authentication
                                .anyRequest().authenticated()
                )
                // jwt-verification filter
                .addFilter(jwtAuthorizationFilter(authManager))
                // exception handling for unauthorized requests
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                // allow cross-origin requests for all endpoints
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // disable CSRF
                .csrf(AbstractHttpConfigurer::disable)
                // this disables session creation on Spring Security
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    private JWTAuthorizationFilter jwtAuthorizationFilter(AuthenticationManager authManager) {
        return new JWTAuthorizationFilter(authManager, secret);
    }

    private CorsConfigurationSource corsConfigurationSource() {
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }
}

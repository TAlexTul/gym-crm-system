package com.epam.gymcrmsystemapi.config.security;

import com.epam.gymcrmsystemapi.Routes;
import com.epam.gymcrmsystemapi.config.loggingattempt.BruteForceProtectionFilter;
import com.epam.gymcrmsystemapi.config.loggingattempt.CustomAuthenticationProvider;
import com.epam.gymcrmsystemapi.config.security.filters.JWTAuthenticationFilter;
import com.epam.gymcrmsystemapi.config.security.filters.JWTAuthorizationFilter;
import com.epam.gymcrmsystemapi.config.security.properties.SecurityProperties;
import com.epam.gymcrmsystemapi.model.user.request.UserSaveRequest;
import com.epam.gymcrmsystemapi.service.loggingattempt.LoggingAttemptOperations;
import com.epam.gymcrmsystemapi.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    private final SecurityProperties securityProperties;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;
    private final LoggingAttemptOperations loggingAttemptOperations;

    public SecurityConfig(
            SecurityProperties securityProperties,
            UserService userService,
            PasswordEncoder passwordEncoder,
            ObjectMapper objectMapper,
            LoggingAttemptOperations loggingAttemptOperations) {
        this.securityProperties = securityProperties;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.objectMapper = objectMapper;
        this.loggingAttemptOperations = loggingAttemptOperations;
    }

    @PostConstruct
    public void init() {
        setupDefaultAdmins();
    }

    private void setupDefaultAdmins() {
        List<UserSaveRequest> requests = securityProperties.getAdmins().entrySet().stream()
                .map(entry -> new UserSaveRequest(
                        entry.getValue().getFirstName(),
                        entry.getValue().getLastName(),
                        new String(entry.getValue().getPassword()),
                        entry.getKey()))
                .peek(admin -> log.info("Default admin found: {}", admin.username()))
                .collect(Collectors.toList());
        userService.mergeAdmins(requests);
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userService).passwordEncoder(passwordEncoder);
        authenticationManagerBuilder.authenticationProvider(customAuthenticationProvider());
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
                                //allow trainee registration and refresh, ignore authorization filters on login
                                .requestMatchers(HttpMethod.POST, Routes.TRAINEES, Routes.TOKEN + "/refresh").permitAll()
                                // allow trainer registration and refresh, ignore authorization filters on login
                                .requestMatchers(HttpMethod.POST, Routes.TRAINERS, Routes.TOKEN + "/refresh").permitAll()
                                // admin can manage login data by id
                                .requestMatchers(Routes.LOGIN + "/{id:\\d+}/**").hasRole("ADMIN")
                                // admin can manage trainee by id
                                .requestMatchers(Routes.TRAINEES + "/{id:\\d+}/**").hasRole("ADMIN")
                                // admin can manage trainers by id
                                .requestMatchers(Routes.TRAINERS + "/{id:\\d+}/**").hasRole("ADMIN")
                                // admin can change trainee`s status by username
                                .requestMatchers(HttpMethod.PATCH, Routes.TRAINEES + "/status").hasRole("ADMIN")
                                // admin can change trainee`s trainer set
                                .requestMatchers(HttpMethod.PATCH, Routes.TRAINEES + "/change").hasRole("ADMIN")
                                // admin can change trainer`s status by username
                                .requestMatchers(HttpMethod.PATCH, Routes.TRAINERS + "/status").hasRole("ADMIN")
                                // admin can get not-assigned trainers by trainee
                                .requestMatchers(HttpMethod.GET, Routes.TRAINERS + "/not-assigned").hasRole("ADMIN")
                                // trainee can manage trainee`s account
                                .requestMatchers(Routes.TRAINEES + "/account/**").hasRole("TRAINEE")
                                // trainer can manage trainer`s account
                                .requestMatchers(Routes.TRAINERS + "/account/**").hasRole("TRAINER")
                                // admin can use Actuator endpoints
                                .requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole("ADMIN")
                                // by default, require authentication
                                .anyRequest().authenticated()
                )
                // logging attempt filter
                .addFilterBefore(bruteForceProtectionFilter(), UsernamePasswordAuthenticationFilter.class)
                // auth filter
                .addFilter(jwtAuthenticationFilter(authManager))
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

    private CustomAuthenticationProvider customAuthenticationProvider() {
        return new CustomAuthenticationProvider(userService, loggingAttemptOperations, passwordEncoder);
    }

    private BruteForceProtectionFilter bruteForceProtectionFilter() {
        return new BruteForceProtectionFilter(loggingAttemptOperations);
    }

    private JWTAuthenticationFilter jwtAuthenticationFilter(AuthenticationManager authManager) {
        var filter = new JWTAuthenticationFilter(authManager, objectMapper);
        filter.setFilterProcessesUrl(Routes.TOKEN);
        return filter;
    }

    private JWTAuthorizationFilter jwtAuthorizationFilter(AuthenticationManager authManager) {
        return new JWTAuthorizationFilter(authManager, securityProperties.getJwt());
    }

    private CorsConfigurationSource corsConfigurationSource() {
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }
}

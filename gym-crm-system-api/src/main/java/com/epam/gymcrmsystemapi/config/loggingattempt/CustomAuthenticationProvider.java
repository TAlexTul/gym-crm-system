package com.epam.gymcrmsystemapi.config.loggingattempt;

import com.epam.gymcrmsystemapi.service.loggingattempt.LoggingAttemptOperations;
import com.epam.gymcrmsystemapi.service.user.UserService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserService userService;
    private final LoggingAttemptOperations loggingAttemptOperations;
    private final PasswordEncoder passwordEncoder;

    public CustomAuthenticationProvider(UserService userService,
                                        LoggingAttemptOperations loggingAttemptOperations,
                                        PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.loggingAttemptOperations = loggingAttemptOperations;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        UserDetails user = userService.loadUserByUsername(username);

        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            loggingAttemptOperations.loginSucceeded(username);
            return new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities());
        } else {
            loggingAttemptOperations.loginFailed(username);
            throw new BadCredentialsException("User with user name '" + username + "' entered an incorrect password");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

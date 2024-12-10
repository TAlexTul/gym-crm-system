package com.epam.trainerworkloadapi.config;

import jakarta.servlet.*;
import org.slf4j.MDC;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class TransactionFilter implements Filter {

    private static final String TRANSACTION_ID = "transactionId";
    private static final String USER_ID = "userId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            String transactionId = UUID.randomUUID().toString();
            MDC.put(TRANSACTION_ID, transactionId);

            String userId = getCurrentUserId();
            MDC.put(USER_ID, userId != null ? userId : "NO USER ID");

            chain.doFilter(request, response);
        } finally {
            MDC.remove(TRANSACTION_ID);
            MDC.remove(USER_ID);
        }
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername();
            } else {
                return principal.toString();
            }
        }
        return null;
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }
}

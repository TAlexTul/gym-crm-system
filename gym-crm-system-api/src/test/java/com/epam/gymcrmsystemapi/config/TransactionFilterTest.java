package com.epam.gymcrmsystemapi.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.mockito.Mockito.*;

class TransactionFilterTest {


    private TransactionFilter transactionFilter;
    private FilterChain filterChain;
    private ServletRequest request;
    private ServletResponse response;

    @BeforeEach
    void setUp() {
        transactionFilter = new TransactionFilter();
        filterChain = mock(FilterChain.class);
        request = mock(ServletRequest.class);
        response = mock(ServletResponse.class);
    }

    @Test
    void testDoFilter_WithAuthenticatedUser() throws Exception {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        try (
                MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class);
                MockedStatic<MDC> mockedMDC = mockStatic(MDC.class)
        ) {
            mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(true);

            String username = "authenticatedUser";
            when(authentication.getPrincipal()).thenReturn(
                    new org.springframework.security.core.userdetails.User(
                            username,
                            "",
                            Collections.emptyList())
            );

            mockedMDC.when(() -> MDC.put(eq("transactionId"), anyString())).then(invocation -> null);
            mockedMDC.when(() -> MDC.put(eq("userId"), eq(username))).then(invocation -> null);

            transactionFilter.doFilter(request, response, filterChain);

            verify(filterChain, times(1)).doFilter(request, response);

            mockedMDC.verify(() -> MDC.put(eq("transactionId"), anyString()), times(1));
            mockedMDC.verify(() -> MDC.put(eq("userId"), eq(username)), times(1));
        }
    }

    @Test
    void testDoFilter_WithNoAuthenticatedUser() throws Exception {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        try (
                MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class);
                MockedStatic<MDC> mockedMDC = mockStatic(MDC.class)
        ) {
            mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(false);

            mockedMDC.when(() -> MDC.put(eq("transactionId"), anyString())).then(invocation -> null);
            mockedMDC.when(() -> MDC.put(eq("userId"), eq("NO USER ID"))).then(invocation -> null);

            transactionFilter.doFilter(request, response, filterChain);

            verify(filterChain, times(1)).doFilter(request, response);

            mockedMDC.verify(() -> MDC.put(eq("transactionId"), anyString()), times(1));
            mockedMDC.verify(() -> MDC.put(eq("userId"), eq("NO USER ID")), times(1));
        }
    }

    @Test
    void testDoFilter_ClearsMDCAfterFilterExecution() throws Exception {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        try (
                MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class);
                MockedStatic<MDC> mockedMDC = mockStatic(MDC.class)
        ) {
            mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(true);

            String username = "testUser";
            when(authentication.getPrincipal()).thenReturn(
                    new org.springframework.security.core.userdetails.User(
                            username,
                            "",
                            Collections.emptyList()));

            mockedMDC.when(() -> MDC.put(anyString(), anyString())).then(invocation -> null);
            mockedMDC.when(() -> MDC.remove(anyString())).then(invocation -> null);

            transactionFilter.doFilter(request, response, filterChain);

            verify(filterChain, times(1)).doFilter(request, response);

            mockedMDC.verify(() -> MDC.put(eq("transactionId"), anyString()), times(1));
            mockedMDC.verify(() -> MDC.put(eq("userId"), eq(username)), times(1));

            mockedMDC.verify(() -> MDC.remove("transactionId"), times(1));
            mockedMDC.verify(() -> MDC.remove("userId"), times(1));
        }
    }
}

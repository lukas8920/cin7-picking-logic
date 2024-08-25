package org.kehrbusch.cin7backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class BearerTokenFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(BearerTokenFilter.class);

    private final String bearerToken;
    private final boolean isMockProfile;

    public BearerTokenFilter(String bearerToken, boolean isMockProfile) {
        this.bearerToken = bearerToken;
        this.isMockProfile = isMockProfile;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        // Skip the filter for the H2 console
        if (isMockProfile && request.getRequestURI().startsWith("/h2-console")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.equals("Bearer " + bearerToken)) {
            Authentication authentication = new PreAuthenticatedAuthenticationToken("user", null, null);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            logger.warn("Unauthorized access - " + request.getRemoteAddr());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }
}

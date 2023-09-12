/*
package com.namekart.amp2;

import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Component
@Order(1)
public class TokenLoggingFilter extends OncePerRequestFilter {

    Logger logger = Logger.getLogger("TKL");



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // Extract and log the token here
       */
/* Authentication authentication = JwtAuthenticationTokenFilter.getAuthentication((HttpServletRequest) request);

        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
            String tokenAsString = jwtAuthenticationToken.getToken().getTokenValue();
            System.out.println("JWT Token: " + tokenAsString);
        }*//*

        String token = extractToken(request);
        System.out.println("Received Token: " + token);

        // Continue with the filter chain
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup code if needed
    }

    // Implement a method to extract the token from the request
    private String extractToken(ServletRequest request) {
        // Implement logic to extract the token from the request headers
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String authorizationHeader = httpRequest.getHeader("Authorization");

        if (authorizationHeader != null ) {
            // Extract and return the token part after "Bearer "
            String token = authorizationHeader;
            logger.info(token);
            return token;
        }
logger.info("yoyo");
        return "yoyo";

    }
}
*/

package com.namekart.amp2;

import com.azure.spring.aad.AADOAuth2AuthenticatedPrincipal;
import com.namekart.amp2.Repository.UserRepository;
import com.namekart.amp2.UserEntities.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UserPresenceFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    public UserPresenceFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Retrieve user information from your custom service/repository
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the user exists based on the authentication details
        if (authentication != null && authentication.isAuthenticated()) {
            AADOAuth2AuthenticatedPrincipal ad=(AADOAuth2AuthenticatedPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username =  (ad.getClaim("unique_name")+"");
            try {
               User user= userRepository.findByEmail(username);
               if(user==null)
               {
                   response.setStatus(HttpServletResponse.SC_FORBIDDEN); // or another appropriate status code
                   response.getWriter().write("User not found");
                   throw new ServletException("User Not Found");
                   //return;
               }
                //logger.info(username);
            } catch (UsernameNotFoundException ex) {
                // Handle the case where the user does not exist
                // You can return an error response or throw a UserNotFoundException here
                response.setStatus(HttpServletResponse.SC_FORBIDDEN); // or another appropriate status code
                response.getWriter().write("User not found");
                return;
            }
        }

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request)
            throws ServletException {
        String path = request.getRequestURI();
        return "/syncuser".equals(path)||"/callback/amp".equals(path);
    }
}

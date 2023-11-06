
package com.namekart.amp2;


import com.azure.spring.aad.webapi.AADResourceServerWebSecurityConfigurerAdapter;
import com.namekart.amp2.Repository.UserRepository;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Logger;

@EnableWebSecurity()
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
public class SecurityConfigurer1 extends AADResourceServerWebSecurityConfigurerAdapter {

    Logger logger= Logger.getLogger("WS");
    UserRepository userDetailsService;
    public SecurityConfigurer1(UserRepository userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        //http.addFilterBefore(new UserPresenceFilter(userDetailsService), AuthorizationFilter.class);

        http.csrf().disable().cors().and().authorizeHttpRequests().antMatchers("*").permitAll();//.antMatchers("/callback/amp").permitAll()
               //.anyRequest().authenticated().and().oauth2ResourceServer().jwt();
    }

}

//.antMatchers("*").permitAll();
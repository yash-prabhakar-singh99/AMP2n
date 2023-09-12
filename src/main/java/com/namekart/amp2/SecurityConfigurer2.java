/*
package com.namekart.amp2;


import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
@Configuration
public class SecurityConfigurer2 {


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors().and()//.addFilterBefore(new TokenLoggingFilter(), AbstractPreAuthenticatedProcessingFilter.class)
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer((oauth2) -> oauth2
                        .jwt(*/
/*jwt -> jwt
                                .jwkSetUri("https://login.windows.net/common/discovery/keys")*//*
));
        return http.build();
    }
   */
/* @Bean
    JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = (NimbusJwtDecoder)
                JwtDecoders.fromIssuerLocation("https://sts.windows.net/eba2c098-631c-4978-8326-5d25c2d09ca5/");

        OAuth2TokenValidator<Jwt> audienceValidator = audienceValidator();
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer("https://sts.windows.net/eba2c098-631c-4978-8326-5d25c2d09ca5/");
        OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);

        jwtDecoder.setJwtValidator(withAudience);

        return jwtDecoder;
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors().and().authorizeRequests(authz -> authz.antMatchers(HttpMethod.GET, "/fetch123")
                        .hasAuthority("SCOPE_Files.Edit")
                        .anyRequest()
                        .authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt());
        return http.build();
    }

    @Bean
    @Order(1)
    public FilterRegistrationBean<TokenLoggingFilter> loggingFilter() {
        FilterRegistrationBean<TokenLoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TokenLoggingFilter());
        registrationBean.addUrlPatterns("/fetch123"); // Specify the URL patterns to apply the filter
        return registrationBean;
    }

      static class AudienceValidator implements OAuth2TokenValidator<Jwt> {
        OAuth2Error error = new OAuth2Error("custom_code", "Custom error message", null);

        @Override
        public OAuth2TokenValidatorResult validate(Jwt jwt) {
            System.out.println(jwt.getTokenValue());
            if (jwt.getAudience().contains("messaging")) {
                return OAuth2TokenValidatorResult.success();
            } else {
                return OAuth2TokenValidatorResult.failure(error);
            }
        }


// ...

    }

    OAuth2TokenValidator<Jwt> audienceValidator() {
        return new AudienceValidator();
    }*//*

}
*/

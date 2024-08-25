package org.kehrbusch.cin7backend.security;

import com.azure.security.keyvault.secrets.SecretClient;
import org.kehrbusch.cin7backend.util.Profile;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Locale;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final String PROD_TOKEN_QUALIFIER = "cin7-bearer-token";
    private static final String TEST_TOKEN = "cin7.bearer.token";

    private static final String ACTIVE_PROFILE = "spring.profiles.active";

    @Bean(name = "bearerToken")
    public String bearerToken(ApplicationContext applicationContext, Environment env){
        String profile = env.getProperty(ACTIVE_PROFILE);
        return profile != null && profile.toUpperCase(Locale.ROOT).equals(Profile.PROD.toString())
                ? applicationContext.getBean(SecretClient.class).getSecret(PROD_TOKEN_QUALIFIER).getValue()
                : env.getProperty(TEST_TOKEN);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, @Qualifier("bearerToken") String bearerToken, Environment env) throws Exception {
        String profile = env.getProperty(ACTIVE_PROFILE);
        boolean isMockProfile = profile != null && profile.equals("mock");
        http
                .addFilterBefore(new BearerTokenFilter(bearerToken, isMockProfile), UsernamePasswordAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz -> {
                    if(isMockProfile){
                        authz.requestMatchers("/h2-console/**").permitAll();
                    }
                    authz.anyRequest().authenticated();
                })
                .headers(headers -> {
                    if (isMockProfile){
                        headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin);
                    }
                });

        return http.build();
    }

    @Bean
    public AuthenticationManager noopAuthenticationManager() {
        return authentication -> {
            throw new AuthenticationServiceException("Authentication is disabled");
        };
    }
}
